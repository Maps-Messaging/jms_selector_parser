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

package io.mapsmessaging.selector.operators.functions.ml;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.clustering.*;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.decisiontree.DecisionTreeOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.isolationforest.IsolationForestOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.lda.LDAOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.linearregression.LassoRegressionOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.linearregression.OlsRegressionOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.linearregression.RidgeRegressionOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.logisticregression.LogisticRegressionOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.mlp.MLPOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.naivebayes.NaiveBayesOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.pca.PCACorOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.pca.PCAFitOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.qda.QDAOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.randomforest.RandomForestOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.tensorflow.TensorFlowOperation;
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
  private final String operationName;
  private final long sampleSize;
  private final long sampleTime;
  private final List<String> identifiers;
  private String modelName;

  public MLFunction(String functionName, List<String> list) {
    this.functionName = functionName;
    identifiers = new ArrayList<>();
    int startIdx = 1;
    if (list.size() > 1) {
      this.operationName = list.get(0);
      this.modelName = list.get(1);
      startIdx = 2;
    } else {
      this.modelName = list.getFirst();
      this.operationName = "";
    }
    for (int i = startIdx; i < list.size(); i++) {
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
    try {
      switch (functionName.toLowerCase()) {
        case "k-means":
          return new KMeansClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "g-means":
          return new GMeansClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "x-means":
          return new XMeansClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "k-means_lloyd":
          return new KMeansLloydClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "ridge":
          return new RidgeRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "ols":
        case "linear_regression":
          return new OlsRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "lasso":
          return new LassoRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "decision_tree":
          return new DecisionTreeOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "naive_bayes":
          return new NaiveBayesOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "pca":
        case "pca_fit":
          return new PCAFitOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "pca_cor":
          return new PCACorOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "random_forest":
          return new RandomForestOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "logistic_regression":
          return new LogisticRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "isolation_forest":
          return new IsolationForestOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "mlp":
          return new MLPOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "qda":
          return new QDAOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "lda":
          return new LDAOperation(modelName, operationName, identifiers, sampleTime, sampleSize);
        case "one_class_svm":
        case "svm":
        case "knn":
          throw new UnsupportedOperationException("Not yet implemented: " + functionName);
        case "tensorflow":
          return new TensorFlowOperation(modelName, identifiers);
        case "model_exists":
          return new io.mapsmessaging.selector.operators.functions.ml.impl.functions
              .ModelExistFunction(modelName);
        default:
          throw new UnsupportedOperationException("Unknown ML function: " + functionName);
      }
    } catch (Exception e) {
      throw new UnsupportedOperationException("ML Function failed to load : " + functionName, e);
    }
  }

  public String toString() {
    return functionName + "(" + modelName + ")";
  }
}
