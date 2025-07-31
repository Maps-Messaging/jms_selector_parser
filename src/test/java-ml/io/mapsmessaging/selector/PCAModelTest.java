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
import io.mapsmessaging.selector.ml.ModelStore;
import io.mapsmessaging.selector.ml.impl.store.FileModelStore;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class PCAModelTest {

  private static final String[] SELECTORS = {
      "pca (applypca[2], scd41.arff , CO₂,  temperature, humidity)< 2 OR NOT model_exists(scd41.arff)",
      "pca (explainedvariance[1], scd41.arff , CO₂,  temperature, humidity)< 1 OR NOT model_exists(scd41.arff)",
      "pca_fit (applypca[2], scd41.arff , CO₂,  temperature, humidity)< 2 OR NOT model_exists(scd41.arff)",
      "pca_fit (explainedvariance[1], scd41.arff , CO₂,  temperature, humidity)< 1 OR NOT model_exists(scd41.arff)",
      "pca_cor (applypca[2], scd41.arff , CO₂,  temperature, humidity)< 2 OR NOT model_exists(scd41.arff)",
      "pca_cor (explainedvariance[1], scd41.arff , CO₂,  temperature, humidity)< 1 OR NOT model_exists(scd41.arff)"
  };

  static Stream<String> selectorProvider() {
    return Stream.of(SELECTORS);
  }

  private ModelStore previous;

  @BeforeEach
  void setUp() {
    previous = MLFunction.getModelStore();
    MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
  }

  @AfterEach
  void tearDown() {
    MLFunction.setModelStore(previous);
  }

  @Test
  void testModelExistsAndLoads() throws Exception {
    Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41.arff"));
    Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41.arff"));
  }

  @ParameterizedTest
  @MethodSource("selectorProvider")
  void testSelectorParses(String selector) throws Exception {
    ParserExecutor executor = SelectorParser.compile(selector);
    Assertions.assertNotNull(executor);
  }

  @ParameterizedTest
  @MethodSource("selectorProvider")
  void testSelectorExecutes(String selector) throws Exception {
    ParserExecutor executor = SelectorParser.compile(selector);
    Assertions.assertTrue(executor.evaluate((IdentifierResolver) key -> {
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
    }));
  }
}
