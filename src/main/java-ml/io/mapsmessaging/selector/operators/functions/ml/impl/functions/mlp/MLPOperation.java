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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.mlp;

import io.mapsmessaging.selector.operators.functions.ml.LabeledDataMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import io.mapsmessaging.selector.ml.ModelStore;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import smile.classification.MLP;
import smile.data.DataFrame;

public class MLPOperation extends LabeledDataMLModelOperation {
  private final MLPFunction operation;
  private MLP mlp;

  public MLPOperation(
      String modelName, String operationName, List<String> identity, long time, long samples, ModelStore modelStore)
      throws ModelException, IOException {
    super(modelName, identity, time, samples, modelStore);
    this.operation = computeFunction(operationName);
  }

  private static MLPFunction computeFunction(String op) throws ModelException {
    if (op.equalsIgnoreCase("predictprob")) {
      return new PredictProbFunction();
    } else if (op.equalsIgnoreCase("predict")) {
      return new PredictFunction();
    }
    throw new ModelException("Expected either <predict> or <predictprob> received " + op);
  }

  @Override
  protected void initializeSpecificModel() {
    // no model to create
  }

  @Override
  public void buildModel(DataFrame data) throws ModelException {
    String labelColumn = prepareLabeledTrainingData(data);

    int[] labels = data.column(labelColumn).toIntArray();
    DataFrame x = data.drop(labelColumn);

    Properties props = new Properties();
    props.setProperty("smile.mlp.layers", "ReLU(100)");
    props.setProperty("smile.mlp.epochs", "50");
    props.setProperty("smile.mlp.mini_batch", "16");

    mlp = MLP.fit(x.toArray(), labels, props);
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute(mlp, data);
  }

  @Override
  public String toString() {
    return "mlp (" + operation.getName() + ", " + super.toString() + ")";
  }
}
