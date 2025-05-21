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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.decisiontree;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import smile.classification.DecisionTree;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.type.StructType;

public class DecisionTreeOperation extends AbstractMLModelOperation {
  private final DecisionTreeFunction decisionTreeFunction;
  private DecisionTree decisionTree;
  private StructType schema;

  public DecisionTreeOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    decisionTreeFunction = computeFunction(operationName);
  }

  private static DecisionTreeFunction computeFunction(String operation) {
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
    var formula = Formula.lhs(labelColumn);
    decisionTree = DecisionTree.fit(formula, data);
    schema = data.schema();
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return decisionTreeFunction.compute(schema, decisionTree, data);
  }

  @Override
  public String toString() {
    return "decision_tree(" + decisionTreeFunction.getName() + "," + super.toString() + ")";
  }
}
