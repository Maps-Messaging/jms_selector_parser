/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2025 ] MapsMessaging B.V.
 *
 *  Licensed under the Apache License, Version 2.0 with the Commons Clause
 *  (the "License"); you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      https://commonsclause.com/
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.onnx;


import ai.onnxruntime.*;
import ai.onnxruntime.OrtSession.SessionOptions;
import io.mapsmessaging.selector.model.ModelStore;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OnnxModelRegistry {

  private static final Map<String, OnnxModelEntry> REGISTRY = new ConcurrentHashMap<>();
  private static final OrtEnvironment ORT_ENVIRONMENT = OrtEnvironment.getEnvironment();

  // Detect once per process; this is the node's preferred engine.
  private static final ModelEngine NODE_PREFERRED_ENGINE = OnnxEngineDetector.getOrDetect();

  // Fixed fallback order; preferred first, then others, CPU last.
  private static final ModelEngine[] FALLBACK_ORDER = buildFallbackOrder(NODE_PREFERRED_ENGINE);

  public static OnnxModelEntry get(final String modelName) {
    return REGISTRY.get(modelName);
  }

  public static OnnxModelEntry getOrLoad(final String modelName,
                                         final ModelStore modelStore) throws OrtException, IOException {
    if (!OnnxRuntimeGate.AVAILABLE) {
      throw new OrtException("ONNX disabled/unavailable on this node");
    }

    OnnxModelEntry cached = REGISTRY.get(modelName);
    if (cached != null) {
      return cached;
    }

    byte[] modelBytes = modelStore.loadModel(modelName);

    // Try engines in the precomputed order until a session is created.
    OrtSession ortSession = null;
    ModelEngine chosenEngine = null;

    for (ModelEngine candidate : FALLBACK_ORDER) {
      SessionOptions sessionOptions = new SessionOptions();
      try {
        appendEngine(sessionOptions, candidate);
        ortSession = ORT_ENVIRONMENT.createSession(modelBytes, sessionOptions);
        chosenEngine = candidate;
        break;
      } catch (Throwable t) {
        // Close options and continue to next candidate
        try {
          sessionOptions.close();
        } catch (Throwable ignore) {
        }
        //log.warn("ONNX: failed to create session for model={} using engine={} - {}", modelName, candidate, t.toString());
      }
    }

    if (ortSession == null) {
      // Final fallback: CPU with default options (should always succeed)
      SessionOptions cpuOptions = new SessionOptions();
      try {
        ortSession = ORT_ENVIRONMENT.createSession(modelBytes, cpuOptions);
        chosenEngine = ModelEngine.ONNX_CPU;
      } catch (OrtException e) {
        try {
          cpuOptions.close();
        } catch (Throwable ignore) {
        }
        throw e;
      }
    }

    // Resolve I/O metadata
    String inputName = resolveSingleInputName(ortSession);
    String outputName = resolvePrimaryOutputName(ortSession);

    TensorInfo tensorInfo = (TensorInfo) ortSession.getInputInfo().get(inputName).getInfo();
    OnnxJavaType inputType = tensorInfo.type;
    long[] inputShape = tensorInfo.getShape();

    long featureCount = resolveFeatureCount(inputShape);
    boolean dynamicBatchAllowed = isDynamicBatch(inputShape);

    OnnxModelEntry entry = OnnxModelEntry.builder()
        .ortSession(ortSession)
        .inputName(inputName)
        .outputName(outputName)
        .inputType(inputType)
        .featureCount(featureCount)
        .dynamicBatchAllowed(dynamicBatchAllowed)
        .engine(chosenEngine)               // <— record it
        .build();
    return REGISTRY.computeIfAbsent(modelName, k -> entry);
  }

  private static String resolveSingleInputName(final OrtSession session) {
    return session.getInputNames().iterator().next();
  }

  private static String resolvePrimaryOutputName(final OrtSession session) {
    return session.getOutputNames().iterator().next();
  }

  private static boolean isDynamicBatch(final long[] inputShape) {
    if (inputShape == null || inputShape.length < 2) {
      return false;
    }
    long batchDim = inputShape[0];
    return batchDim == -1L || batchDim == 0L;
  }

  private static long resolveFeatureCount(final long[] inputShape) {
    if (inputShape == null || inputShape.length < 2) {
      throw new IllegalArgumentException("Input tensor must be rank-2 [batch, features].");
    }
    long features = inputShape[1];
    if (features <= 0) {
      throw new IllegalArgumentException("Model input has non-positive feature dimension.");
    }
    return features;
  }

  private static void appendEngine(final SessionOptions sessionOptions,
                                   final ModelEngine modelEngine) throws OrtException {
    switch (modelEngine) {
      case ONNX_TRT: {
        sessionOptions.addTensorrt(0);
        try {
          sessionOptions.addCUDA(); // fallback for unsupported ops
        } catch (Throwable ignore) {
        }
        break;
      }
      case ONNX_CUDA: {
        sessionOptions.addCUDA();
        break;
      }
      case ONNX_QNN: {
        appendQnnExecutionProvider(sessionOptions);
        break;
      }
      case ONNX_CPU:
      default: {
        // CPU requires no explicit EP configuration
        break;
      }
    }
  }
  private static void appendQnnExecutionProvider(final OrtSession.SessionOptions opts) throws OrtException {
    // Read from sysprop/env (override these as needed)
    final String backendPath = pick("maps.qnn.backend_path", "QNN_BACKEND_PATH",
        "/usr/lib/libQnnHtp.so"); // e.g., QnnHtp.so / libQnnHtp.so / QnnCpu.so
    final String cacheEnable = pick("maps.qnn.cache_enable", "QNN_CONTEXT_CACHE_ENABLE", "1");
    final String cachePath   = pick("maps.qnn.cache_path",   "QNN_CONTEXT_CACHE_PATH",   "/var/cache/ort_qnn");
    final String profiling   = pick("maps.qnn.profiling",    "QNN_PROFILING_LEVEL",      "basic"); // off|basic|detailed

    java.util.Map<String,String> qnn = new java.util.HashMap<>();
    qnn.put("backend_path", backendPath);
    qnn.put("qnn_context_cache_enable", cacheEnable);
    qnn.put("qnn_context_cache_path", cachePath);
    qnn.put("profiling_level", profiling);
    // Append QNN EP
    opts.addQnn ( qnn);
  }

  private static String pick(String sysProp, String envVar, String defVal) {
    String v = System.getProperty(sysProp);
    if (v != null && !v.isBlank()) return v;
    v = System.getenv(envVar);
    return (v != null && !v.isBlank()) ? v : defVal;
  }

  private static ModelEngine[] buildFallbackOrder(final ModelEngine preferred) {
    // Preferred first, then the others, CPU last.
    if (preferred == ModelEngine.ONNX_TRT) {
      return new ModelEngine[]{ModelEngine.ONNX_TRT, ModelEngine.ONNX_CUDA, ModelEngine.ONNX_QNN, ModelEngine.ONNX_CPU};
    }
    if (preferred == ModelEngine.ONNX_CUDA) {
      return new ModelEngine[]{ModelEngine.ONNX_CUDA, ModelEngine.ONNX_TRT, ModelEngine.ONNX_QNN, ModelEngine.ONNX_CPU};
    }
    if (preferred == ModelEngine.ONNX_QNN) {
      return new ModelEngine[]{ModelEngine.ONNX_QNN, ModelEngine.ONNX_CUDA, ModelEngine.ONNX_TRT, ModelEngine.ONNX_CPU};
    }
    return new ModelEngine[]{ModelEngine.ONNX_CPU};
  }

  private OnnxModelRegistry() {}
}