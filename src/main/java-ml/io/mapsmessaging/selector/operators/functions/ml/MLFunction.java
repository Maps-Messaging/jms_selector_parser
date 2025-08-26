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
import io.mapsmessaging.selector.model.ModelStore;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.clustering.*;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.decisiontree.DecisionTreeOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.isolationforest.IsolationForestOperation;
import io.mapsmessaging.selector.operators.functions.ml.impl.functions.knn.KNNOperation;
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
import io.mapsmessaging.selector.ml.impl.store.MapModelStore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class MLFunction extends Operation {

  @Getter
  @Setter
  private static int defaultSampleSize = 100;

  @Getter
  @Setter
  private static int defaultSampleTime = 0;

  @Getter
  @Setter
  private static ModelStore modelStore = new MapModelStore();

  private final String functionName;
  private final String operationName;
  private final long sampleSize;
  private final long sampleTime;
  private final List<String> identifiers;
  private final String modelName;

  protected MLFunction(String functionName, String operationName, String modelName, List<String> identifiers, long sampleSize, long sampleTime) {
    this.functionName = functionName;
    this.operationName = operationName;
    this.modelName = modelName;
    this.identifiers = identifiers;
    this.sampleSize = sampleSize;
    this.sampleTime = sampleTime;
  }

  public static MLFunction parse(String functionName, List<String> list) {
    String operationName = "";
    String modelName;
    List<String> identifiers = new ArrayList<>();
    int startIdx;

    if ("tensorFlow".equalsIgnoreCase(functionName) || list.size() <=1) {
      modelName = list.getFirst();
      startIdx = 1;
    } else {
      operationName = list.get(0);
      modelName = list.get(1);
      startIdx = 2;
    }

    for (int i = startIdx; i < list.size(); i++) {
      identifiers.add(list.get(i).trim());
    }

    return new MLFunction(functionName, operationName, modelName, identifiers, defaultSampleSize, defaultSampleTime);
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
          return new KMeansClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "g-means":
          return new GMeansClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "x-means":
          return new XMeansClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "k-means_lloyd":
          return new KMeansLloydClusterOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "ridge":
          return new RidgeRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "ols","linear_regression":
          return new OlsRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "lasso":
          return new LassoRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "decision_tree":
          return new DecisionTreeOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "naive_bayes":
          return new NaiveBayesOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "pca","pca_fit":
          return new PCAFitOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "pca_cor":
          return new PCACorOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "random_forest":
          return new RandomForestOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "logistic_regression":
          return new LogisticRegressionOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "isolation_forest":
          return new IsolationForestOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "mlp":
          return new MLPOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "qda":
          return new QDAOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "lda":
          return new LDAOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "knn":
          return new KNNOperation(modelName, operationName, identifiers, sampleTime, sampleSize, modelStore);
        case "svm", "one_class_svm":
          throw new UnsupportedOperationException("Not yet implemented: " + functionName);
        case "tensorflow":
          return new TensorFlowOperation(modelName, identifiers, modelStore);
        case "model_exists":
          return new io.mapsmessaging.selector.operators.functions.ml.impl.functions.ModelExistFunction(modelName, modelStore);
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
