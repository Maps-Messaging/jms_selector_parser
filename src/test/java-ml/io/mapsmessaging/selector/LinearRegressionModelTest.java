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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class LinearRegressionModelTest {

  private static Stream<String> selectors() {
    return Stream.of(
        "linear_regression (predict, scd41_alt.arff , CO₂,  temperature, humidity) > 0 OR NOT model_exists(scd41.arff)",
        "ols (predict, scd41_alt.arff , CO₂,  temperature, humidity) > 0 OR NOT model_exists(scd41.arff)",
        "ridge (predict, scd41_alt.arff , CO₂,  temperature, humidity) > 0 OR NOT model_exists(scd41.arff)",
        "lasso (predict, scd41_alt.arff , CO₂,  temperature, humidity) > 0 OR NOT model_exists(scd41.arff)"
    );
  }

  @ParameterizedTest
  @MethodSource("selectors")
  void testSelectorParser(String selector) throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41_alt.arff"));
      Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41_alt.arff"));

      ParserExecutor executor = SelectorParser.compile(selector);
      Assertions.assertNotNull(executor);
    } finally {
      MLFunction.setModelStore(previous);
    }
  }

  @Test
  void testRunModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      ParserExecutor executor = SelectorParser.compile("linear_regression (predict, scd41_alt.arff , CO₂,  temperature, humidity) < 50");
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

      Assertions.assertFalse(executor.evaluate((IdentifierResolver) key -> {
        switch (key) {
          case "CO₂":
            return 1200;
          case "temperature":
            return 30.9;
          case "humidity":
            return 30.4;
          default:
            return Double.NaN;
        }
      }));
    } finally {
      MLFunction.setModelStore(previous);
    }
  }
}
