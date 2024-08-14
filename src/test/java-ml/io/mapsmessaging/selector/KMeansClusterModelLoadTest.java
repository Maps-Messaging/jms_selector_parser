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

class KMeansClusterModelLoadTest {


  @Test
  void testLoadModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41.arff"));
      Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41.arff"));
      ParserExecutor executor = SelectorParser.compile("K-means_clustering (distance, scd41.arff , CO₂ , temperature, humidity) > 0 OR NOT model_exists(scd41.arff)");
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
      ParserExecutor executor = SelectorParser.compile("K-means_clustering (distance, scd41.arff , CO₂ , temperature, humidity) < 2");
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
            return 20.9;
          case "humidity":
            return 55.6;
          default:
            return Double.NaN;
        }
      }));
    } finally {
      MLFunction.setModelStore(previous);
    }
  }



}
