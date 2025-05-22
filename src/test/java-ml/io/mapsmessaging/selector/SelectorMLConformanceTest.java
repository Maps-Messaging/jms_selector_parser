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

package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class SelectorMLConformanceTest {

  public static Stream<String> selectors() {
    return Stream.of(
        "model_exists(example_model.arff) = true",

        // Clustering
        "k-means (distance, model_kmeans.arff) > 1.0",
        "k-means (distance, model_kmeans.arff, temp, humidity) > 1.0",
        "g-means (distance, model_gmeans.arff) > 1.0",
        "g-means (distance, model_gmeans.arff, temp, humidity) > 1.0",
        "x-means (distance, model_xmeans.arff) > 1.0",
        "x-means (distance, model_xmeans.arff, temp, humidity) > 1.0",
        "k-means_lloyd (distance, model_lloyd.arff) > 1.0",
        "k-means_lloyd (distance, model_lloyd.arff, temp, humidity) > 1.0",

        // Regression
        "ols (predict, model_ols.arff, temp, humidity) < 30.0",
        "ridge (predict, model_ridge.arff, temp, humidity) < 30.0",
        "lasso (predict, model_lasso.arff, temp, humidity) < 30.0",

        // Classification
        "decision_tree (classify, model_dt.arff, temp, humidity) = 1",
        "naive_bayes (classify, model_nb.arff, temp, humidity) = 1",
        "random_forest (classify, model_rf.arff, temp, humidity) = 1",

        "isolation_forest (score, model_iso.arff) = 1",
        "isolation_forest (score, model_iso.arff, temp, humidity) = 1",
        "isolation_forest (is_anomaly, model_iso.arff) = 1",
        "isolation_forest (is_anomaly, model_iso.arff, temp, humidity) = 1",

        "logistic_regression (classify, model_logreg.arff, temp, humidity) = 1",
        "logistic_regression (classifyprob, model_logreg.arff, temp, humidity) = 1",

        "mlp (predict, model_mlp.arff, temp, humidity) = 1",
        "mlp (predictprob, model_mlp.arff, temp, humidity) = 1",
        "qda (predict, model_qda.arff, temp, humidity) = 1",
        "qda (predictprob, model_qda.arff, temp, humidity) = 1",
        "lda (predict, model_lda.arff, temp, humidity) = 1",
        "lda (predictprob, model_lda.arff, temp, humidity) = 1",
        "knn (classify, model_knn.arff, temp, humidity) = 1",

        /*

        "svm (classify, model_svm.arff) = 1",
        "svm (classify, model_svm.arff, temp, humidity) = 1",

        // Anomaly Detection
        "one_class_svm (anomaly, model_ocsvm.arff) = 1",
        "one_class_svm (anomaly, model_ocsvm.arff, temp, humidity) = 1",
*/
        // PCA
        "pca_fit (explainedvariance[1], model_pca_fit.arff) > 0.7",
        "pca_fit (explainedvariance[2], model_pca_fit.arff, temp, humidity) > 0.7",
        "pca_cor (explainedvariance[3], model_pca_cor.arff) > 0.7",
        "pca_cor (explainedvariance[4], model_pca_cor.arff, temp, humidity) > 0.7"
    );

  }

  @ParameterizedTest(name = "Syntax test for: {0}")
  @MethodSource("selectors")
  void syntaxTest(String selector) {
    try {
      Constants.setThreshold(0.000000001);
      ParserExecutor parser = SelectorParser.compile(selector);
      Assertions.assertNotNull(parser.toString());
    } catch (ParseException e) {
      Assertions.fail("Selector text failed: " + selector + " with exception: " + e.getMessage());
    }
  }

  @ParameterizedTest(name = "Equality test for: {0}")
  @MethodSource("selectors")
  void selectorEqualityTests(String selector) {
    try {
      Object parser1 = SelectorParser.compile(selector);
      Object parser2 = SelectorParser.compile(selector);
      Assertions.assertEquals(parser1.toString(), parser2.toString());
      String t = parser1.toString();
      t = t.substring(1);
      t = t.replace("(true)", "true");
      t = t.replace("))", ")");
      t = t.replace("==", "=");
      t = stripTrailingParens(t);
      Assertions.assertEquals(t, selector);
    } catch (ParseException|UnsupportedOperationException e) {
      e.printStackTrace();
      Assertions.fail("Selector text failed: " + selector + " with exception: " + e.getMessage());
    }
  }

  public static String stripTrailingParens(String input) {
    int lastParenOpen = input.lastIndexOf('(');
    int lastParenClose = input.lastIndexOf(')');
    if (lastParenOpen >= 0 && lastParenClose == input.length() - 1 && lastParenOpen < lastParenClose) {
      return input.substring(0, lastParenOpen) + input.substring(lastParenOpen + 1, lastParenClose);
    }
    return input;
  }

}
