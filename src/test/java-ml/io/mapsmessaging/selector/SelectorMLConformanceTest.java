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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class SelectorMLConformanceTest {

  public static final String[] SELECTOR_TEXT =
      {
          "K-means_clustering ( distance, scd41, CO₂, temperature, humidity ) > 1.9",
          "K-means_clustering ( wcss, home_temp_K_means_model, temperature, humidity ) > 1.9",
          "model_exists(home_temp_K_means_model) = true",
          "decision_tree (classify, home_temp_decision_model, temperature, humidity, pressure) = 1",
          "linear_regression (predict,home_temp_regression_model, temperature, humidity, pressure) < 50.0",
          "K-means_clustering (distance, home_temp_K_means_model, temperature, humidity, pressure) > 1.9 and time <> 12",
          "linear_regression (predict, home_temp_regression_model, temperature, humidity, pressure) < 50.0 OR ( time = 11 )",
          "decision_tree (classify, home_temp_decision_model, temperature, humidity, pressure) = 1 and phone not like '12%3'",
      };

  @Test
  void syntaxTest() {
    for (String selector : SELECTOR_TEXT) {
      try {
        Constants.setThreshold(0.000000001);
        ParserExecutor parser = SelectorParser.compile(selector);
        parser.toString();
      } catch (ParseException e) {
        e.printStackTrace();
        Assertions.fail("Selector text:" + selector + " failed with exception " + e.getMessage());
      }
    }
  }

  @Test
  void selectorEqualityTests() {
    for (String selector : SELECTOR_TEXT) {
      try {
        Object parser1 = SelectorParser.compile(selector);
        Object parser2 = SelectorParser.compile(selector);
        Assertions.assertEquals(parser1.toString(), parser2.toString());
        if(!(selector.contains("K-means_clustering") ||
            selector.contains("decision_tree") ||
            selector.contains("model_exists") ||
            selector.contains("linear_regression"))) {
          Assertions.assertEquals(parser1.hashCode(), parser2.hashCode());
        }
      } catch (ParseException e) {
        Assertions.fail("Selector text:" + selector + " failed with exception " + e.getMessage());
      }
    }
  }
}
