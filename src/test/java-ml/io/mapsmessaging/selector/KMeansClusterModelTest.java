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
import io.mapsmessaging.selector.operators.functions.ml.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.FileModelStore;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.MapModelStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class KMeansClusterModelTest {
  private static ModelStore previous;

  private static final String[] SELECTORS = {
      "k-means (centroid[1], scd41.arff , CO₂ , temperature, humidity) > 19 OR NOT model_exists(scd41.arff)",
      "k-means (distance, scd41.arff , CO₂ , temperature, humidity) > 15.0 OR NOT model_exists(scd41.arff)",
      "k-means (clusterlabel, scd41.arff , CO₂ , temperature, humidity) = 2 OR NOT model_exists(scd41.arff)",
      "k-means (wcss, scd41.arff , CO₂ , temperature, humidity) > 190 OR NOT model_exists(scd41.arff)",
      "k-means (clustersizes[2], scd41.arff , CO₂ , temperature, humidity) = 5035 OR NOT model_exists(scd41.arff)",
      "k-means (totalclusters, scd41.arff , CO₂ , temperature, humidity) = 3 OR NOT model_exists(scd41.arff)",

      "g-means (centroid[1], scd41.arff , CO₂ , temperature, humidity) > 19 OR NOT model_exists(scd41.arff)",
      "g-means (distance, scd41.arff , CO₂ , temperature, humidity) > 15.0 OR NOT model_exists(scd41.arff)",
      "g-means (clusterlabel, scd41.arff , CO₂ , temperature, humidity) = 1 OR NOT model_exists(scd41.arff)",
      "g-means (wcss, scd41.arff , CO₂ , temperature, humidity) > 190 OR NOT model_exists(scd41.arff)",
      "g-means (clustersizes[2], scd41.arff , CO₂ , temperature, humidity) = 1956 OR NOT model_exists(scd41.arff)",
      "g-means (totalclusters, scd41.arff , CO₂ , temperature, humidity) = 3 OR NOT model_exists(scd41.arff)",

      "x-means (centroid[1], scd41.arff , CO₂ , temperature, humidity) > 19 OR NOT model_exists(scd41.arff)",
      "x-means (distance, scd41.arff , CO₂ , temperature, humidity) > 15.0 OR NOT model_exists(scd41.arff)",
      "x-means (clusterlabel, scd41.arff , CO₂ , temperature, humidity) = 1 OR NOT model_exists(scd41.arff)",
      "x-means (wcss, scd41.arff , CO₂ , temperature, humidity) > 190 OR NOT model_exists(scd41.arff)",
      "x-means (clustersizes[0], scd41.arff , CO₂ , temperature, humidity) = 3123 OR NOT model_exists(scd41.arff)",
      "x-means (totalclusters, scd41.arff , CO₂ , temperature, humidity) = 3 OR NOT model_exists(scd41.arff)",

      "k-means_lloyd (centroid[1], scd41.arff , CO₂ , temperature, humidity) > 19 OR NOT model_exists(scd41.arff)",
      "k-means_lloyd (distance, scd41.arff , CO₂ , temperature, humidity) > 15.0 OR NOT model_exists(scd41.arff)",
      "k-means_lloyd (clusterlabel, scd41.arff , CO₂ , temperature, humidity) = 2 OR NOT model_exists(scd41.arff)",
      "k-means_lloyd (wcss, scd41.arff , CO₂ , temperature, humidity) > 190 OR NOT model_exists(scd41.arff)",
      "k-means_lloyd (clustersizes[1], scd41.arff , CO₂ , temperature, humidity) > 100 OR NOT model_exists(scd41.arff)",
      "k-means_lloyd (totalclusters, scd41.arff , CO₂ , temperature, humidity) = 3 OR NOT model_exists(scd41.arff)",

  };

  static Stream<String> selectorProvider() {
    return Stream.of(SELECTORS);
  }

  @BeforeAll
  static void setupStore() {
    previous = MLFunction.getModelStore();
    MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
  }

  @AfterAll
  static void restoreStore() {
    MLFunction.setModelStore(previous);
  }

  @Test
  void testCompileSelector() throws Exception {
    Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41.arff"));
    Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41.arff"));
    ParserExecutor executor = SelectorParser.compile(SELECTORS[0]);
    Assertions.assertNotNull(executor);
    Assertions.assertFalse(executor.toString().isEmpty());
  }

  @ParameterizedTest(name = "Evaluate Selector: {0}")
  @MethodSource("selectorProvider")
  void testEvaluateSelector(String selector) throws Exception {
    smile.math.MathEx.setSeed(10);
    ParserExecutor executor = SelectorParser.compile(selector);
    Assertions.assertTrue(
        executor.evaluate(
            (IdentifierResolver)
                key -> {
                  switch (key) {
                    case "CO₂":
                      return 566;
                    case "temperature":
                      return 20.9;
                    case "humidity":
                      return 55.6;
                    default:
                      return Double.NaN;
                  }
                }),"Selector failed: " + selector);

  }
}
