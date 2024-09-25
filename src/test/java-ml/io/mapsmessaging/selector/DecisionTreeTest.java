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

package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.functions.ml.MLFunction;
import io.mapsmessaging.selector.operators.functions.ml.ModelStore;
import io.mapsmessaging.selector.operators.functions.ml.impl.store.FileModelStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DecisionTreeTest {


  private final static String[] SELECTORS ={
      "decision_tree (classify, scd41_alt.arff , CO₂,  temperature, humidity, CO₂_level) > 0 OR NOT model_exists(scd41_alt.arff)",
      "decision_tree (classifyprob, scd41_alt.arff , CO₂,  temperature, humidity, CO₂_level) > 0 OR NOT model_exists(scd41_alt.arff)",

  } ;


  @Test
  void testLoadModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41_alt.arff"));
      Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41_alt.arff"));
      for(String selector : SELECTORS) {
        ParserExecutor executor = SelectorParser.compile(selector);
        Assertions.assertNotNull(executor);
      }
    } finally {
      MLFunction.setModelStore(previous);
    }
  }

  @Test
  void testRunModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      ParserExecutor executor = SelectorParser.compile("decision_tree (classify, scd41_alt.arff , CO₂,  temperature, humidity, CO₂_level)< 50");
      Assertions.assertTrue(executor.evaluate((IdentifierResolver) key -> {
        switch (key) {
          case "CO₂":
            return 566;
          case "temperature":
            return 20.9;
          case "humidity":
            return 55.6;
          case "CO₂_level":
            return "Low";
          default:
            return Double.NaN;
        }
      }));
      executor = SelectorParser.compile("decision_tree (classifyprob, scd41_alt.arff , CO₂,  temperature, humidity, CO₂_level)< 50");
      Assertions.assertTrue(executor.evaluate((IdentifierResolver) key -> {
        switch (key) {
          case "CO₂":
            return 566;
          case "temperature":
            return 20.9;
          case "humidity":
            return 55.6;
          case "CO₂_level":
            return "Low";
          default:
            return Double.NaN;
        }
      }));

    } finally {
      MLFunction.setModelStore(previous);
    }
  }
}
