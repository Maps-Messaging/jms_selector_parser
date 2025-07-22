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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.lda;

import io.mapsmessaging.selector.operators.functions.ml.LabeledDataMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.List;

import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import smile.classification.LDA;
import smile.data.DataFrame;

public class LDAOperation extends LabeledDataMLModelOperation {
  private final LDAFunction operation;
  private LDA lda;

  public LDAOperation(
      String modelName, String operationName, List<String> identity, long time, long samples, ModelStore modelStore)
      throws ModelException, IOException {
    super(modelName, identity, time, samples, modelStore);
    this.operation = computeFunction(operationName);
  }

  private static LDAFunction computeFunction(String op) throws ModelException {
    if (op.equalsIgnoreCase("predictprob")) {
      return new PredictProbFunction();
    } else if (op.equalsIgnoreCase("predict")) {
      return new PredictFunction();
    }
    throw new ModelException("Expected <predictprob> or <predictprob> received [" + op + "]");
  }

  @Override
  protected void initializeSpecificModel() {
    // no model to initialise
  }

  @Override
  public void buildModel(DataFrame data) throws ModelException {
    String labelColumn = prepareLabeledTrainingData(data);
    int[] labels = data.column(labelColumn).toIntArray();
    DataFrame x = data.drop(labelColumn);

    lda = LDA.fit(x.toArray(), labels);
    isModelTrained = true;
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute(lda, data);
  }

  @Override
  public String toString() {
    return "lda (" + operation.getName() + ", " + super.toString() + ")";
  }
}
