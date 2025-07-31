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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LdaModelTest {
  private static ModelStore previous;
  private static final double[][] testData = {
      {1.0, 2.0},     // expected class 0
      {8.0, 9.0},     // expected class 1
      {1.1, 1.9},     // expected class 0
      {7.9, 9.1}      // expected class 1
  };
  private static final int[] expected = {0, 1, 0, 1};

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
  void testLdaClassification() throws ParseException {
    ParserExecutor executor;
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(testData);
    for (int i = 0; i < expected.length; i++) {
      executor = SelectorParser.compile("lda(predict, ldaTest.arff, a0, a1) = " + expected[i]);
      Assertions.assertTrue(executor.evaluate(resolver), "Failed at index " + i);
      resolver.index++;
    }
  }

  @Test
  void testLdaPredictProb() throws ParseException {
    ParserExecutor executor;
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(testData);
    double[] thresholds = {0.86, 0.84, 0.86, 0.84}; // Expected probability for class 1

    for (int i = 0; i < expected.length; i++) {
      executor = SelectorParser.compile("lda(predictprob, ldaTest.arff, a0, a1) > " + thresholds[i]);
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

