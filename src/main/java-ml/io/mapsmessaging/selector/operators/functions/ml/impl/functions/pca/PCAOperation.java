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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.pca;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PCAOperation extends AbstractMLModelOperation {
  private final PCAFunction pcaFunction;
  private AttributeSelection filter;

  public PCAOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    pcaFunction = computeFunction(operationName);
  }

  private static PCAFunction computeFunction(String operation) {
    if (operation.toLowerCase().startsWith("applypca[")) {
      String indexStr = operation.substring(9, operation.length() - 1); // Extract the index
      int index = Integer.parseInt(indexStr);
      return new ApplyPCAFunction(index);
    }
    if (operation.equalsIgnoreCase("explainedvariance")) {
      return new ExplainedVarianceFunction();
    }
    throw new UnsupportedOperationException("Unknown operation: " + operation);
  }

  @Override
  protected void initializeSpecificModel() {
    // Adding attributes based on the identity
    ArrayList<Attribute> attributes = new ArrayList<>();
    for (String s : identity) {
      attributes.add(new Attribute(s));
    }
    structure = new Instances(modelName, attributes, 0);
    filter = new AttributeSelection();
  }

  @Override
  protected void buildModel(Instances trainingData) throws ModelException {
    // Set up the PrincipalComponents evaluator
    PrincipalComponents pca = new PrincipalComponents();
    pca.setVarianceCovered(0.95); // For example, keep 95% of variance

    // Set up the Ranker search method
    Ranker ranker = new Ranker();
    ranker.setNumToSelect(-1);

    // Set up the AttributeSelection filter
    filter.setEvaluator(pca);
    filter.setSearch(ranker);
    try {
      filter.setInputFormat(trainingData);

      // Apply the filter to the training data to initialize it
      Filter.useFilter(trainingData, filter);
      isModelTrained = true;
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  protected double applyModel(Instance instance) throws ModelException {
    try {
      return pcaFunction.compute(filter, instance);
    } catch (Exception e) {
      throw new ModelException(e);
    }
  }

  @Override
  public String toString() {
    return "PCA(" + pcaFunction.getName() + "," + super.toString() + ")";
  }
}
