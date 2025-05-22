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

import io.mapsmessaging.selector.operators.functions.ml.LabeledDataMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.*;
import smile.classification.NaiveBayes;
import smile.data.DataFrame;
import smile.data.measure.NominalScale;
import smile.stat.distribution.Distribution;

public class NaiveBayesOperation extends LabeledDataMLModelOperation {
  private final NaiveBayesFunction naiveBayesFunction;
  private NaiveBayes naiveBayes;

  public NaiveBayesOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    naiveBayesFunction = computeFunction(operationName);
  }

  private static NaiveBayesFunction computeFunction(String operation) throws ModelException {
    switch (operation.toLowerCase()) {
      case "classifyprob":
        return new ClassifyProbFunction();
      case "classify":
        return new ClassifyFunction();
      default:
        throw new ModelException("Expected either <classify> or <classifyprob> received " +operation);
    }
  }

  @Override
  protected void initializeSpecificModel() {}

  @Override
  public String toString() {
    return "naive_bayes (" + naiveBayesFunction.getName() + ", " + super.toString() + ")";
  }

  @Override
  public void buildModel(DataFrame dataFrame) throws ModelException {
    String labelColumn = prepareLabeledTrainingData(dataFrame);
    // Ensure label column has nominal scale
    var field = dataFrame.schema().field(labelColumn);
    if (!(field.measure() instanceof NominalScale)) {
      throw new ModelException("Label column must be nominal.");
    }
    int[] y = dataFrame.column(labelColumn).toIntArray();
    double[][] x = dataFrame.drop(labelColumn).toArray();

    int k = ((NominalScale) field.measure()).size(); // number of classes
    int p = x[0].length; // number of features

    // Calculate priors
    double[] priors = new double[k];
    for (int label : y) priors[label]++;
    for (int i = 0; i < k; i++) priors[i] /= y.length;

    // Compute conditional Gaussians
    Distribution[][] condprob = new Distribution[k][p];
    for (int cls = 0; cls < k; cls++) {
      List<double[]> rows = new ArrayList<>();
      for (int i = 0; i < x.length; i++) {
        if (y[i] == cls) rows.add(x[i]);
      }

      for (int j = 0; j < p; j++) {
        double[] column = new double[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
          column[i] = rows.get(i)[j];
        }
        double mean = Arrays.stream(column).average().orElse(0.0);
        double std = Math.sqrt(Arrays.stream(column).map(v -> Math.pow(v - mean, 2)).average().orElse(1e-6));
        condprob[cls][j] = new smile.stat.distribution.GaussianDistribution(mean, std);
      }
    }
    naiveBayes = new NaiveBayes(priors, condprob);
  }

  @Override
  public double applyModel(double[] data) {
    return naiveBayesFunction.compute(naiveBayes, data);
  }
}
