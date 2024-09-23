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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.naivebayes;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class NaiveBayesOperation extends AbstractMLModelOperation {
  private NaiveBayes naiveBayes;
  private final NaiveBayesFunction naiveBayesFunction;

  public NaiveBayesOperation(String modelName, String operationName, List<String> identity, long time, long samples) throws Exception {
    super(modelName, identity, time, samples);
    naiveBayesFunction = computeFunction(operationName);
  }

  @Override
  protected void initializeSpecificModel() throws Exception {
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
  protected void buildModel(Instances trainingData) throws Exception {
    naiveBayes.buildClassifier(trainingData);
    isModelTrained = true;
  }

  @Override
  protected double applyModel(Instance instance) throws Exception {
    return naiveBayesFunction.compute(naiveBayes, instance);
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
