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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tensorflow.*;
import org.tensorflow.ndarray.Shape;

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
        float[] result = new float[1];
        outputTensor.asRawTensor().data().asFloats().read(result);
        return result[0];
      }
    }
  }

  protected void loadModel() throws ModelException {
    modelEntry = TensorFlowModelRegistry.getOrLoad(modelName, store);
    isModelTrained = modelEntry != null;
  }

  @Override
  public String toString() {
    return "tensorflow ("+ super.toString() + ")";
  }

}
