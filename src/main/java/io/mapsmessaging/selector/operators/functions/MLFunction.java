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

package io.mapsmessaging.selector.operators.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.*;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.MapModelStore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class MLFunction extends Operation {

  @Getter @Setter private static int defaultSampleSize = 100;

  @Getter @Setter private static int defaultSampleTime = 0;

  @Getter @Setter private static ModelStore modelStore = new MapModelStore();

  private final String functionName;
  private final String modelName;
  private final long sampleSize;
  private final long sampleTime;
  private final List<String> identifiers;

  public MLFunction(String functionName, List<String> list) {
    this.functionName = functionName;
    this.modelName = list.get(0);
    identifiers = new ArrayList<>();
    for (int i = 1; i < list.size(); i++) {
      identifiers.add(list.get(i).trim());
    }
    sampleSize = defaultSampleSize;
    sampleTime = defaultSampleTime;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    return true;
  }

  @Override
  public Object compile() {
    switch (functionName.toLowerCase()) {
      case "k-means_clustering":
        return new KMeansClusterOperation(modelName, identifiers, sampleTime, sampleSize);
      case "linear_regression":
        return new LinearRegressionOperation(modelName, identifiers, sampleTime, sampleSize);
      case "decision_tree":
        return new DecisionTreeOperation(modelName, identifiers, sampleTime, sampleSize);
      case "naive_bayes":
        return new NaiveBayesOperation(modelName, identifiers, sampleTime, sampleSize);
      case "hierarchical_clustering":
        return new HierarchicalClusterOperation(modelName, identifiers, sampleTime, sampleSize);
      case "pca":
        return new PCAOperation(modelName, identifiers, sampleTime, sampleSize);
      case "tensorflow":
        return new TensorFlowOperation(modelName, identifiers);
      case "model_exists":
        return new ModelExistFunction(modelName);
      default:
        throw new UnsupportedOperationException("Unknown ML function: " + functionName);
    }
  }

  public String toString() {
    return functionName + "(" + modelName + ")";
  }
}
