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

class KMeansClusterModelTest {

    private final static String[] SELECTORS ={
        "K-means_clustering (distance, scd41.arff , CO₂ , temperature, humidity) < 1.5 OR NOT model_exists(scd41.arff)",
        "K-means_clustering (clusterlabel, scd41.arff , CO₂ , temperature, humidity) = 2 OR NOT model_exists(scd41.arff)",
        "K-means_clustering (centroid[1], scd41.arff , CO₂ , temperature, humidity) > 20 OR NOT model_exists(scd41.arff)",
        "K-means_clustering (wcss, scd41.arff , CO₂ , temperature, humidity) > 579 OR NOT model_exists(scd41.arff)",
        "K-means_clustering (clustersizes[0], scd41.arff , CO₂ , temperature, humidity) = 1914 OR NOT model_exists(scd41.arff)",
        "K-means_clustering (totalclusters, scd41.arff , CO₂ , temperature, humidity) = 3 OR NOT model_exists(scd41.arff)",
        "K-means_clustering (silhouettescore, scd41.arff , CO₂ , temperature, humidity) = 1 OR NOT model_exists(scd41.arff)",
    } ;


  @Test
  void testLoadModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      Assertions.assertTrue(MLFunction.getModelStore().modelExists("scd41.arff"));
      Assertions.assertNotNull(MLFunction.getModelStore().loadModel("scd41.arff"));
      for(String selector : SELECTORS) {
        ParserExecutor executor = SelectorParser.compile(selector);
        Assertions.assertNotNull(executor);
        Assertions.assertNotNull(executor.toString());
        Assertions.assertFalse(executor.toString().isEmpty());
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
      for (String selector : SELECTORS) {
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
                    }));

      }
    } finally {
      MLFunction.setModelStore(previous);
    }
  }



}
