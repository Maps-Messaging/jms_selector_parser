/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2026 ] MapsMessaging B.V.
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
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.model.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.AbstractModelOperations;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OnnxOperation extends AbstractModelOperations {

  private final String modelName;
  private final List<String> identity;
  private final ModelStore modelStore;

  private OnnxModelEntry onnxModelEntry;
  private volatile boolean warmedUp;

  public OnnxOperation(String modelName, List<String> identity, ModelStore modelStore) {
    super(modelName, identity);
    this.modelName = modelName;
    this.identity = identity;
    this.modelStore = modelStore;

    try {
      initializeIfNeeded();
    } catch (OrtException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public synchronized void initializeIfNeeded()
      throws OrtException, IOException {
    if (onnxModelEntry != null) {
      return;
    }
    this.onnxModelEntry = OnnxModelRegistry.getOrLoad(modelName, modelStore);
    this.warmUp();
  }

  @Override
  public Object evaluate(final IdentifierResolver resolver) throws ParseException {
    if (onnxModelEntry == null) {
      throw new IllegalStateException("Model not initialized: " + modelName);
    }

    // 1) Resolve features from selector (same as TF path)
    final Object[] features = evaluateList(resolver);
    final long expected = onnxModelEntry.getFeatureCount();
    if (features.length != expected) {
      throw new IllegalArgumentException(
          "Model expects " + expected + " features, but got " + features.length);
    }

    try {
      final OrtEnvironment environment = OrtEnvironment.getEnvironment();
      try (OnnxTensor input =
          OnnxTensorBuilder.createTensorFromFeatures(
              environment,
              onnxModelEntry.getInputType(),
              onnxModelEntry.getFeatureCount(),
              features)) {

        final Map<String, OnnxTensor> inputs =
            java.util.Collections.singletonMap(onnxModelEntry.getInputName(), input);

        // 3) Run and extract first scalar (mirrors TF path)
        try (OrtSession.Result result = onnxModelEntry.getOrtSession().run(inputs)) {
          final OnnxValue value =
              result
                  .get(onnxModelEntry.getOutputName())
                  .orElseThrow(
                      () ->
                          new IllegalStateException(
                              "Output not found: " + onnxModelEntry.getOutputName()));

          try (OnnxTensor out = (OnnxTensor) value) {
            final TensorInfo info = (TensorInfo) out.getInfo();
            switch (info.type) {
              case FLOAT:
                {
                  final java.nio.FloatBuffer buf = out.getFloatBuffer();
                  return buf.hasRemaining() ? buf.get(0) : 0f;
                }
              case DOUBLE:
                {
                  final java.nio.DoubleBuffer buf = out.getDoubleBuffer();
                  return buf.hasRemaining() ? buf.get(0) : 0d;
                }
              case INT32:
                {
                  final java.nio.IntBuffer buf = out.getIntBuffer();
                  return buf.hasRemaining() ? buf.get(0) : 0;
                }
              case INT64:
                {
                  final java.nio.LongBuffer buf = out.getLongBuffer();
                  return buf.hasRemaining() ? buf.get(0) : 0L;
                }
              case BOOL:
                {
                  final Object raw = out.getValue(); // boolean[], boolean[][], etc.
                  if (raw instanceof boolean[][] b2) return b2[0][0];
                  if (raw instanceof boolean[] b1) return b1[0];
                  throw new IllegalArgumentException("Unsupported BOOL output shape");
                }
              default:
                throw new IllegalArgumentException("Unsupported output type: " + info.type);
            }
          }
        }
      }
    } catch (OrtException e) {
      ParseException ex = new ParseException();
      ex.initCause(e);
      throw ex;
    }
  }


  protected Object[] evaluateList(IdentifierResolver resolver) {
    Object[] dataset = new Object[identity.size()];
    for (int x = 0; x < identity.size(); x++) {
      dataset[x] = resolver.get(identity.get(x));
    }
    return dataset;
  }

  private void warmUp() throws OrtException {
    if (warmedUp) {
      return;
    }
    OrtEnvironment environment = OrtEnvironment.getEnvironment();

    long featureCount = onnxModelEntry.getFeatureCount();
    float[] zeros = new float[(int) featureCount];

    try (OnnxTensor input =
             OnnxTensor.createTensor(environment,
                 java.nio.FloatBuffer.wrap(zeros),
                 new long[]{1L, featureCount})) {
      Map<String, OnnxTensor> inputs =
          java.util.Collections.singletonMap(onnxModelEntry.getInputName(), input);
      try (OrtSession.Result ignored = onnxModelEntry.getOrtSession().run(inputs)) {
        this.warmedUp = true;
      }
    }
  }

  @Override
  public String toString() {
    return "onnx ("+ super.toString() + ")";
  }
}
