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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TensorflowTest {
  private final static String[] SELECTORS ={
      "tensorflow (sensor_safety_model, temperature, humidity, CO₂) < 0.5  OR NOT model_exists(sensor_safety_model)",
  } ;


  @Test
  void testLoadModel() throws Exception {
    ModelStore previous = MLFunction.getModelStore();
    try {
      MLFunction.setModelStore(new FileModelStore("./src/test/resources/"));
      Assertions.assertTrue(MLFunction.getModelStore().modelExists("sensor_safety_model"));
      Assertions.assertNotNull(MLFunction.getModelStore().loadModel("sensor_safety_model.zip"));
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
      ParserExecutor executor = SelectorParser.compile(SELECTORS[0]);
      Assertions.assertTrue(executor.evaluate(new TestIdentityResolver(587,20.9,55.6)));
    } finally {
      MLFunction.setModelStore(previous);
    }
  }
}
