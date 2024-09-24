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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.decisiontree;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecisionTreeOperation extends AbstractMLModelOperation {
  private J48 decisionTree;
  private final DecisionTreeFunction decisionTreeFunction;

  public DecisionTreeOperation(String modelName, String operationName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, identity, time, samples);
    decisionTreeFunction = computeFunction(operationName);
  }

  @Override
  protected void initializeSpecificModel()  {
    // Adding attributes based on the identity
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    structure.setClassIndex(structure.numAttributes() - 1);
    decisionTree = new J48();
  }

  @Override
  protected void buildModel(Instances trainingData) throws ModelException {
    trainingData.setClassIndex(trainingData.numAttributes() - 1);
    try {
      decisionTree.buildClassifier(trainingData);
      isModelTrained = true;
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  protected double applyModel(Instance instance) throws ModelException {
    try {
      return decisionTreeFunction.compute(decisionTree, instance);
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String toString() {
    return "DecisionTree(" + super.toString() + ")";
  }

  private static DecisionTreeFunction computeFunction(String operation){
    switch (operation.toLowerCase()) {
      case "classifyprob":
        return new ClassifyProbFunction();
      case "classify":
      default:
        return new ClassifyInstanceFunction();
    }
  }
}
