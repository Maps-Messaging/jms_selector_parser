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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.linearregression;

import io.mapsmessaging.selector.operators.functions.ml.LabeledDataMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import io.mapsmessaging.selector.ml.ModelStore;
import java.io.IOException;
import java.util.List;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.LinearModel;

public abstract class LinearRegressionOperation extends LabeledDataMLModelOperation {

  private final LinearRegressionFunction linearRegressionFunction;
  private LinearModel linearModel;

  protected LinearRegressionOperation(
      String modelName, String operationName, List<String> identity, long time, long samples, ModelStore modelStore)
      throws ModelException, IOException {
    super(modelName, identity, time, samples, modelStore);
    linearRegressionFunction = computeFunction(operationName);
  }

  private static LinearRegressionFunction computeFunction(String operation) throws ModelException {
    if (operation.equalsIgnoreCase("predict")) {
      return new PredictFunction();
    }
    throw new ModelException("Expected <predict> received [" + operation+"]");
  }

  @Override
  public String toString() {
    return getName() + " (" + linearRegressionFunction.getName() + ", " + super.toString() + ")";
  }

  @Override
  public void buildModel(DataFrame dataFrame) throws ModelException {
    String labelColumn = prepareLabeledTrainingData(dataFrame);
    Formula formula = Formula.lhs(labelColumn);
    linearModel = generate(formula, dataFrame);
    isModelTrained = true;
  }

  protected abstract LinearModel generate(Formula formula, DataFrame dataFrame);

  protected abstract String getName();

  @Override
  public double applyModel(double[] data) {
    return linearRegressionFunction.compute(linearModel, data);
  }

  @Override
  protected void initializeSpecificModel() {}
}
