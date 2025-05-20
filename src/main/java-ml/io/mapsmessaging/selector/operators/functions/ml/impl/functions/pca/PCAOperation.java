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
import io.mapsmessaging.selector.operators.functions.ml.impl.SmileFunction;
import smile.data.DataFrame;
import smile.feature.extraction.PCA;

import java.io.IOException;
import java.util.List;

public class PCAOperation extends AbstractMLModelOperation implements SmileFunction {
  private final PCAFunction pcaFunction;
  private PCA pca;

  public PCAOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    pcaFunction = computeFunction(operationName);
  }

  private static PCAFunction computeFunction(String operation) {
    if (operation.toLowerCase().startsWith("applypca[")) {
      int index = extractIndex(operation);
      return new ApplyPCAFunction(index);
    }
    if (operation.startsWith("explainedvariance")) {
      int index = extractIndex(operation);
      return new ExplainedVarianceFunction(index);
    }
    throw new UnsupportedOperationException("Unknown operation: " + operation);
  }

  private static int extractIndex(String function){
    int start = function.indexOf("[");
    int end = function.indexOf("]");
    return Integer.parseInt(function.substring(start + 1, end));
  }

  @Override
  public void buildModel(DataFrame trainingData)  {
    pca = PCA.fit(trainingData, trainingData.names()).getProjection(trainingData.names().length);;
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data)  {
    return pcaFunction.compute(pca, data);
  }

  @Override
  public String toString() {
    return "PCA(" + pcaFunction.getName() + "," + super.toString() + ")";
  }

  @Override
  protected void initializeSpecificModel() throws ModelException {

  }
}
