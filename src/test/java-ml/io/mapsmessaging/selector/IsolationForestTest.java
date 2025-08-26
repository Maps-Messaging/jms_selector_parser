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
import io.mapsmessaging.selector.model.ModelStore;
import io.mapsmessaging.selector.ml.impl.store.FileModelStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class IsolationForestTest {
  private static ModelStore previous;
  private static final double[][] testData = {
      {1.0, 1.0},    // expected normal
      {130.0, 100.0},  // expected outlier
      {0.9,1.1},   // expected normal
      {15.0, 15.0}   // expected outlier
  };
  boolean[] expected = {false, true, false, true};

  private static final String[] scoreSelectors={
      "isolation_forest(score, isoTest.arff, a0, a1 ) > 0.35",
      "isolation_forest(score, isoTest.arff, a0, a1 ) > 0.55",
      "isolation_forest(score, isoTest.arff, a0, a1 ) > 0.35",
      "isolation_forest(score, isoTest.arff, a0, a1 ) > 0.55"
  };

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
  void testIsolationForestModel() throws ParseException {
    ParserExecutor executor;
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(testData);
    executor = SelectorParser.compile("isolation_forest(is_anomaly, isoTest.arff, a0, a1 ) > 0");
    for (int i = 0; i < expected.length; i++) {
      boolean result = executor.evaluate(resolver);
      boolean expectedResult = expected[i];
      Assertions.assertEquals(expectedResult, result, "Failed at index " + i);
      resolver.index++;
    }
  }

  @Test
  void testIsolationForestModelScore() throws ParseException {
    ParserExecutor executor;
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(testData);
    for (int i = 0; i < expected.length; i++) {
      executor = SelectorParser.compile(scoreSelectors[i]);
      Assertions.assertTrue(executor.evaluate(resolver), "Failed at index " + i);
      resolver.index++;
    }
  }


  static class ArrayIdentifierResolver implements IdentifierResolver {
    private final double[][] data;
    protected int index = 0;

    public ArrayIdentifierResolver(double[][] data) {
      this.data = data;
    }

    @Override
    public Object get(String key) {
      key = key.substring(1);
      int idx = Integer.parseInt(key);
      return data[index][idx];
    }

    public List<String> getKeys() {
      return new ArrayList<>(Arrays.asList("a0", "a1"));
    }
  }
}
