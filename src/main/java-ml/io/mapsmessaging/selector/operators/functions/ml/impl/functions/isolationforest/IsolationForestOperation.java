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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.isolationforest;

import io.mapsmessaging.selector.operators.functions.ml.AbstractMLModelOperation;
import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import smile.anomaly.IsolationForest;
import smile.data.DataFrame;

public class IsolationForestOperation extends AbstractMLModelOperation {
  private final IsolationForestFunction operation;
  private IsolationForest isolationForest;
  private double threshold;

  public IsolationForestOperation(
      String modelName, String operationName, List<String> identity, long time, long samples)
      throws ModelException, IOException {
    super(modelName, identity, time, samples);
    operation = computeFunction(operationName);
  }

  private static IsolationForestFunction computeFunction(String operation) {
    switch (operation.toLowerCase()) {
      case "is_anomaly":
        return new IsAnomalyFunction();
      case "score":
      default:
        return new ScoreFunction();
    }
  }

  @Override
  protected void initializeSpecificModel() {}

  @Override
  public void buildModel(DataFrame data) {
    String[] names = data.names();
    if (identity.isEmpty()) {
      identity.addAll(Arrays.asList(names).subList(0, names.length - 1));
    }
    isolationForest = IsolationForest.fit(data.toArray());
    computeThreshold(data);
    isModelTrained = true;
  }

  private void computeThreshold(DataFrame data){
    double[] scores = Arrays.stream(data.toArray())
        .mapToDouble(isolationForest::score)
        .toArray();

    double mean = Arrays.stream(scores).average().orElse(0);
    double stddev = Math.sqrt(Arrays.stream(scores).map(x -> Math.pow(x - mean, 2)).average().orElse(0));
    threshold = mean + 2 * stddev;  // 2σ above mean = likely anomaly
  }

  @Override
  public double applyModel(double[] data) {
    return operation.compute( isolationForest, data, threshold);
  }

  @Override
  public String toString() {
    return "isolation_forest(" + operation.getName() + "," + super.toString() + ")";
  }
}
