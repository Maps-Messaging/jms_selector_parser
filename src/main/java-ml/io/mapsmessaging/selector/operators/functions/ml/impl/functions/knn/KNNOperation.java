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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.knn;

import io.mapsmessaging.selector.operators.functions.ml.LabeledDataMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.*;
import smile.classification.KNN;
import smile.data.DataFrame;


public class KNNOperation extends LabeledDataMLModelOperation {

  private final KNNFunction knnFunction;
  private KNN<double[]> knn;

  public KNNOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    knnFunction = computeFunction(operationName);
  }

  private static KNNFunction computeFunction(String operation) throws ModelException {
    if (operation.equalsIgnoreCase("classify")) {
      return new ClassifyFunction();
    }
    throw new ModelException("Expected either <classify> received " + operation);
  }

  @Override
  protected void initializeSpecificModel() {
    // Is no model to initialise
  }

  @Override
  public String toString() {
    return "knn (" + knnFunction.getName() + ", " + super.toString() + ")";
  }

  @Override
  public void buildModel(DataFrame dataFrame) throws ModelException {
    String labelColumn = prepareLabeledTrainingData(dataFrame);

    // Optional: ensure label is numeric (e.g., integer class ID)
    var field = dataFrame.schema().field(labelColumn);
    if (!field.dtype().isNumeric()) {
      throw new ModelException("Label column must be numeric for KNN.");
    }

    int[] y = dataFrame.column(labelColumn).toIntArray();
    double[][] x = dataFrame.drop(labelColumn).toArray();

    int k = 3; // or make configurable

    knn = KNN.fit(x, y, k); // trains and stores data
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return knnFunction.compute(knn, data);
  }
}
