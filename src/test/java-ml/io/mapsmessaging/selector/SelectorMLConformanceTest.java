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
        "model_exists(home_temp_K_means_model) = true",
        "k-means ( distance, scd41, CO₂, temperature, humidity ) > 1.9",
        "k-means ( wcss, home_temp_K_means_model, temperature, humidity ) > 1.9",
        "k-means (distance, home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
        "k-means (clusterlabel, home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
        "k-means (centroid[1], home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
        "k-means (clustersizes[0], home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
        "k-means (totalclusters, home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
        "k-means (silhouettescore, home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
        "decision_tree (classify, home_temp_decision_model, temperature, humidity, pressure) = 1",
        "decision_tree (classifyprob, home_temp_decision_model, temperature, humidity, pressure) = 1",
        "linear_regression (predict,home_temp_regression_model, temperature, humidity, pressure) < 50.0",
        "pca (explainedvariance,home_temp_regression_model, temperature, humidity, pressure) < 50.0",
        "pca (applypca[0],home_temp_regression_model, temperature, humidity, pressure) < 50.0",
        "naive_bayes (classify, home_temp_decision_model.arff , CO₂,  temperature, humidity, CO₂_level) > 0",
        "naive_bayes (classifyprob, home_temp_decision_model.arff , CO₂,  temperature, humidity, CO₂_level) > 0",
        "hierarchical (scd41_alt.arff , CO₂,  temperature, humidity, CO₂_level) > 0"
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
    } catch (ParseException e) {
      Assertions.fail("Selector text failed: " + selector + " with exception: " + e.getMessage());
    }
  }
}
