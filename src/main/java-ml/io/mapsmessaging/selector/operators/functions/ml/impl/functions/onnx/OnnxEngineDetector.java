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

import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class OnnxEngineDetector {

  private static volatile ModelEngine cachedEngine;
  private static final Object lock = new Object();

  /** Detect once per process and cache. */
  public static ModelEngine getOrDetect() {
    ModelEngine e = cachedEngine;
    if (e != null) return e;
    synchronized (lock) {
      if (cachedEngine != null) return cachedEngine;
      cachedEngine = detectOnce();
      return cachedEngine;
    }
  }

  /** Apply the cached/ detected engine to the given SessionOptions. */
  public static void applyTo(final OrtSession.SessionOptions opts) throws OrtException {
    appendEngine(opts, getOrDetect());
  }

  // ---------- internals ----------

  private static ModelEngine detectOnce() {
    // 1) explicit override
    ModelEngine forced = readForced();
    if (forced != null && tryAppend(forced)) return forced;

    // 2) heuristic order: TRT → CUDA → QNN → CPU
    if (tryAppend(ModelEngine.ONNX_TRT))  return ModelEngine.ONNX_TRT;
    if (tryAppend(ModelEngine.ONNX_CUDA)) return ModelEngine.ONNX_CUDA;
    if (tryAppend(ModelEngine.ONNX_QNN))  return ModelEngine.ONNX_QNN;

    return ModelEngine.ONNX_CPU;
  }

  private static boolean tryAppend(final ModelEngine engine) {
    try (OrtSession.SessionOptions opts = new OrtSession.SessionOptions()) {
      appendEngine(opts, engine);
      return true;
    } catch (Throwable ignore) {
      return false;
    }
  }

  private static void appendEngine(final OrtSession.SessionOptions opts,
                                   final ModelEngine engine) throws OrtException {
    switch (engine) {
      case ONNX_TRT -> {
        int device = parseInt(sysOrEnv("maps.cuda.device", "MAPS_CUDA_DEVICE"), 0);
        opts.addTensorrt(device);
        safeAddCudaFallback(opts);
      }
      case ONNX_CUDA -> opts.addCUDA();
      case ONNX_QNN  -> opts.addQnn(qnnOptions());
      case ONNX_CPU  -> { /* no-op */ }
    }
  }

  private static void safeAddCudaFallback(final OrtSession.SessionOptions opts) {
    try { opts.addCUDA(); } catch (Throwable ignored) {}
  }

  // QNN options (adjust defaults as needed)
  private static Map<String,String> qnnOptions() {
    Map<String,String> m = new HashMap<>();
    m.put("backend_path", sysOrEnv("maps.qnn.backend_path", "QNN_BACKEND_PATH",
        "/usr/lib/libQnnHtp.so"));            // e.g. libQnnHtp.so / libQnnCpu.so / libQnnGpu.so
    m.put("qnn_context_cache_enable", sysOrEnv("maps.qnn.cache_enable", "QNN_CONTEXT_CACHE_ENABLE", "1"));
    m.put("qnn_context_cache_path",   sysOrEnv("maps.qnn.cache_path",   "QNN_CONTEXT_CACHE_PATH",   "/var/cache/ort_qnn"));
    m.put("profiling_level",          sysOrEnv("maps.qnn.profiling",    "QNN_PROFILING_LEVEL",      "basic")); // off|basic|detailed
    return m;
  }

  private static ModelEngine readForced() {
    String v = sysOrEnv("maps.ml.engine", "MAPS_ML_ENGINE");
    if (v == null || v.isBlank()) return null;
    String s = v.trim().toLowerCase(Locale.ROOT);
    if (s.equals("trt") || s.equals("onnx-trt") || s.equals("tensorrt")) return ModelEngine.ONNX_TRT;
    if (s.equals("cuda")|| s.equals("onnx-cuda")|| s.equals("gpu"))      return ModelEngine.ONNX_CUDA;
    if (s.equals("qnn") || s.equals("onnx-qnn") || s.equals("htp"))      return ModelEngine.ONNX_QNN;
    if (s.equals("cpu") || s.equals("onnx-cpu"))                         return ModelEngine.ONNX_CPU;
    return null;
  }

  private static String sysOrEnv(String sysProp, String envVar) {
    String v = System.getProperty(sysProp);
    if (v != null && !v.isBlank()) return v;
    v = System.getenv(envVar);
    return (v != null && !v.isBlank()) ? v : null;
  }

  private static String sysOrEnv(String sysProp, String envVar, String defVal) {
    String v = sysOrEnv(sysProp, envVar);
    return (v != null) ? v : defVal;
  }

  private static int parseInt(String s, int def) {
    if (s == null) return def;
    try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return def; }
  }

  private OnnxEngineDetector() {}
}
