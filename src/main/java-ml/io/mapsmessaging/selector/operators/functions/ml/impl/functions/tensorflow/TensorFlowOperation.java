/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.tensorflow;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.functions.ml.AbstractModelOperations;
import io.mapsmessaging.selector.operators.functions.ml.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.ModelUtils;
import java.io.IOException;
import java.util.List;
import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.ndarray.buffer.DoubleDataBuffer;
import org.tensorflow.types.TFloat64;

public class TensorFlowOperation extends AbstractModelOperations {
  private SavedModelBundle model;

  public TensorFlowOperation(String modelName, List<String> identity) {
    super(modelName, identity);
    try {
      initializeModel();
    } catch (Exception e) {
      throw new RuntimeException(e);
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

    Tensor inputTensor = createTensor(features);

    // Run the model and fetch the result
    try (Session session = model.session()) {
      Result outputs =
          session.runner().feed("input_tensor_name", inputTensor).fetch("output_tensor_name").run();

      // Retrieve the output tensor and extract the result
      try (Tensor outputTensor = outputs.get(0)) {
        double[] result = new double[(int) outputTensor.shape().size(1)];
        outputTensor.asRawTensor().data().asDoubles().read(result);
        return result[0]; // Adjust this based on your model's output
      }
    }
  }

  protected void loadModel() throws ModelException {
    try {
      byte[] modelData = MLFunction.getModelStore().loadModel(modelName + "_data");
      model = ModelUtils.byteArrayToModel(modelData, "");
      isModelTrained = true;
    } catch (IOException e) {
      throw new ModelException(e);
    }
  }

  private Tensor createTensor(Object[] features) {
    double[] doubleFeatures = new double[features.length];

    for (int i = 0; i < features.length; i++) {
      Object feature = features[i];
      if (feature instanceof Number) {
        doubleFeatures[i] = ((Number) feature).doubleValue();
      } else if (feature instanceof String) {
        try {
          doubleFeatures[i] = Double.parseDouble((String) feature);
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Invalid string input: " + feature, e);
        }
      } else {
        throw new IllegalArgumentException(
            "Unsupported input type: " + feature.getClass().getName());
      }
    }

    // Create DoubleDataBuffer
    DoubleDataBuffer buffer = DataBuffers.of(doubleFeatures);

    // Create the tensor using the buffer
    return TFloat64.tensorOf(Shape.of(1, doubleFeatures.length), buffer);
  }
}
