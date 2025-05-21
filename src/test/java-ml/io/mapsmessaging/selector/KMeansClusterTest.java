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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class KMeansClusterTest {

  private static double[][] trainingData = {
      {5.0, 5.0}, {6.0, 5.0}, {5.0, 6.0}, {6.0, 6.0}, {4.0, 4.0},
      {15.0, 15.0}, {16.0, 15.0}, {15.0, 16.0}, {16.0, 16.0}, {14.0, 14.0},
      {25.0, 25.0}, {26.0, 25.0}, {25.0, 26.0}, {26.0, 26.0}, {24.0, 24.0}
  };

  private static double[][] testData = {
      {6.0, 6.0}, {14.0, 14.0}, {26.0, 26.0}, {260.0, 260.0}
  };

  private static String[] results = {
      "k-means (distance, modelName1) between 1.13 and 1.69",
      "k-means (distance, modelName1) between 1.60 and 1.7",
      "k-means (distance, modelName1) between 1.11 and 1.15",
      "k-means (distance, modelName1) > 16"
  };


  @Test
  void testModel() throws ParseException {
    MLFunction.setDefaultSampleSize(trainingData.length);
    ParserExecutor executor = SelectorParser.compile("k-means (distance, modelName1 , a0 , a1) < 10 OR NOT model_exists(modelName1)");
    ArrayIdentifierResolver resolver = new ArrayIdentifierResolver(trainingData);
    // Train the model with the training data
    while(resolver.index< trainingData.length){
      Assertions.assertTrue(executor.evaluate(resolver));
      resolver.index++;
    }

    // Apply the model to new test data
    resolver = new ArrayIdentifierResolver(testData);
    while(resolver.index< testData.length){
      executor = SelectorParser.compile(results[resolver.index]);
      boolean result = executor.evaluate(resolver);
      Assertions.assertTrue(result, "Failed : "+results[resolver.index]);
      resolver.index++;
    }
  }

  class ArrayIdentifierResolver implements IdentifierResolver {

    private final double[][] data;
    protected int index =0;

    public ArrayIdentifierResolver(double[][] data) {
      this.data = data;
    }

    @Override
    public Object get(String key) {
      key = key.substring(1);
      int idx = Integer.parseInt(key);
      return data[index][idx];
    }
  }


}
