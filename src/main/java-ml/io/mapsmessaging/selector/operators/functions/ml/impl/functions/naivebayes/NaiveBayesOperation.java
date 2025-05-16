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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.naivebayes;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NaiveBayesOperation extends AbstractMLModelOperation {
  private NaiveBayes naiveBayes;
  private final NaiveBayesFunction naiveBayesFunction;

  public NaiveBayesOperation(String modelName, String operationName, List<String> identity, long time, long samples) throws ModelException, IOException {
    super(modelName, identity, time, samples);
    naiveBayesFunction = computeFunction(operationName);
  }

  @Override
  protected void initializeSpecificModel(){
    // Adding attributes based on the identity
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    structure.setClassIndex(structure.numAttributes() - 1);
    naiveBayes = new NaiveBayes();
  }

  @Override
  protected void buildModel(Instances trainingData) throws ModelException {
    trainingData.setClassIndex(structure.numAttributes() - 1);
    try {
      naiveBayes.buildClassifier(trainingData);
      isModelTrained = true;
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  protected double applyModel(Instance instance) throws ModelException {
    try {
      return naiveBayesFunction.compute(naiveBayes, instance);
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String toString() {
    return "NaiveBayes(" + naiveBayesFunction.getName()+","+ super.toString() + ")";
  }

  private static NaiveBayesFunction computeFunction(String operation) {
    switch (operation.toLowerCase()) {
      case "classifyprob":
        return new ClassifyProbFunction();
      case "classify":
      default:
        return new ClassifyFunction();
    }
  }
}
