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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.tensorflow;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.functions.ml.AbstractModelOperations;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tensorflow.*;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.ndarray.buffer.DoubleDataBuffer;
import org.tensorflow.proto.DataType;
import org.tensorflow.types.TFloat64;

@Slf4j
public class TensorFlowOperation extends AbstractModelOperations {
  private final ModelStore store;
  private TensorFlowModelEntry modelEntry;
  

  public TensorFlowOperation(String modelName, List<String> identity, ModelStore modelStore) {
    super(modelName, identity);
    store = modelStore;
    try {
      initializeModel();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
  }

  protected void initializeModel() throws ModelException {
    loadModel();
    isModelTrained = true;
  }

  protected Object[] evaluateList(IdentifierResolver resolver) {
    Object[] dataset = new Object[identity.size()];
    for (int x = 0; x < identity.size(); x++) {
      dataset[x] = resolver.get(identity.get(x));
    }
    return dataset;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object[] features = evaluateList(resolver);
    System.out.println("Running model with input:");
    for (int i = 0; i < features.length; i++) {
      System.out.println("  [" + i + "] = " + features[i]);
    }
    // Validate feature count
    if (features.length != modelEntry.getFeatureCount()) {
      throw new IllegalArgumentException("Model expects " + modelEntry.getFeatureCount() + " features, but got " + features.length);
    }

    // Create the input tensor
    Tensor inputTensor = TensorBuilder.createTensor(
        features,
        modelEntry.getInputDataType(),
        Shape.of(1, modelEntry.getFeatureCount())
    );

    // Run the model and fetch the result
    try (Session session = modelEntry.getModel().session()) {
      Result outputs = session
          .runner()
          .feed(modelEntry.getInputTensorName(), inputTensor)
          .fetch(modelEntry.getOutputTensorName())
          .run();

      try (Tensor outputTensor = outputs.get(0)) {
        Shape shape = outputTensor.shape();
        System.out.println("Output shape: " + Arrays.toString(shape.asArray()));
        float[] result = new float[1];
        outputTensor.asRawTensor().data().asFloats().read(result);
        System.out.println(Arrays.toString(result));
        return result[0];
      }
    }
  }

  protected void loadModel() throws ModelException {
    modelEntry = TensorFlowModelRegistry.getOrLoad(modelName, store);
    isModelTrained = modelEntry != null;
  }

  private Tensor createTensor(Object[] features) {
    double[] doubleFeatures = new double[features.length];

    for (int i = 0; i < features.length; i++) {
      Object feature = features[i];
      if (feature instanceof Number featureNumber) {
        doubleFeatures[i] = featureNumber.doubleValue();
      } else if (feature instanceof String featureString) {
        try {
          doubleFeatures[i] = Double.parseDouble(featureString);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Invalid string input: " + feature, e);
        }
      } else {
        throw new IllegalArgumentException("Unsupported input type: " + feature.getClass().getName());
      }
    }

    // Create DoubleDataBuffer
    DoubleDataBuffer buffer = DataBuffers.of(doubleFeatures);

    // Create the tensor using the buffer
    return TFloat64.tensorOf(Shape.of(1, doubleFeatures.length), buffer);
  }

  public String toString() {
    return "tensorflow ("+ super.toString() + ")";
  }

}
