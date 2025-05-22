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

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import io.mapsmessaging.selector.operators.functions.ml.RawDataMLModelOperation;
import java.io.IOException;
import java.util.List;
import smile.data.DataFrame;
import smile.feature.extraction.PCA;

public abstract class PCAOperation extends RawDataMLModelOperation {
  protected final PCAFunction pcaFunction;
  protected int index;
  private PCA pca;

  public PCAOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    pcaFunction = computeFunction(operationName);
  }

  private static int extractIndex(String function) {
    int start = function.indexOf("[");
    int end = function.indexOf("]");
    if (start == -1 || end == -1) {
      return 0;
    }
    return Integer.parseInt(function.substring(start + 1, end));
  }

  private PCAFunction computeFunction(String operation) throws ModelException {
    if (operation.toLowerCase().startsWith("applypca[")) {
      index = extractIndex(operation);
      return new ApplyPCAFunction(index);
    }
    if (operation.startsWith("explainedvariance")) {
      index = extractIndex(operation);
      return new ExplainedVarianceFunction(index);
    }
    throw new ModelException("Expected either <explainedvariance> or <applypca> received " +operation);
  }

  @Override
  public void buildModel(DataFrame trainingData) {
    pca = create(trainingData);
    isModelTrained = true;
  }

  public abstract PCA create(DataFrame trainingData);

  @Override
  public double applyModel(double[] data) {
    return pcaFunction.compute(pca, data);
  }

  @Override
  public String toString() {
    return "pca_fit (" + pcaFunction.getName() + ", " + super.toString() + ")";
  }

  protected String getSubString() {
    return super.toString();
  }

  @Override
  protected void initializeSpecificModel() throws ModelException {}
}
