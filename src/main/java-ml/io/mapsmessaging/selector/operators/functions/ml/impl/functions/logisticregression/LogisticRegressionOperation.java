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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.logisticregression;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import smile.classification.LogisticRegression;
import smile.data.DataFrame;
import smile.data.formula.Formula;

public class LogisticRegressionOperation extends AbstractMLModelOperation {
  private final LogisiticRegressionFunction operation;
  private LogisticRegression logisticRegression;

  public LogisticRegressionOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    operation = computeFunction(operationName);
  }

  private static LogisiticRegressionFunction computeFunction(String operation) {
    switch (operation.toLowerCase()) {
      case "classifyprob":
        return new ClassifyProbFunction();
      case "classify":
      default:
        return new ClassifyInstanceFunction();
    }
  }

  @Override
  protected void initializeSpecificModel() {}

  @Override
  public void buildModel(DataFrame data) {
    String labelColumn = data.schema().field(data.columns().size() - 1).name();
    String[] names = data.names();
    if(identity.isEmpty()){
      identity.addAll(Arrays.asList(names).subList(0, names.length - 1));
    }

    Formula formula = Formula.lhs(labelColumn);
    double[][] x = formula.x(data).toArray();
    int[] y = formula.y(data).toIntArray();

// Build model — handles binary and multiclass automatically
    logisticRegression = LogisticRegression.fit(x, y);
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute( logisticRegression, data);
  }

  @Override
  public String toString() {
    return "random_forest(" + operation.getName() + "," + super.toString() + ")";
  }
}
