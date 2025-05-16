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

package io.mapsmessaging.selector.operators.functions;

import io.mapsmessaging.selector.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BetweenOperatorTest {


  @Test
  void simpleValidation() throws ParseException {
    BetweenOperator betweenOperator = new BetweenOperator(12L, 10L, 20L);
    Assertions.assertTrue((Boolean) betweenOperator.evaluate(null), "Failed on numeric tests");

    betweenOperator = new BetweenOperator(12L, 15L, 20L);
    Assertions.assertFalse((Boolean) betweenOperator.evaluate(null), "Failed on numeric tests");

    // This can not be evaluated since strings can not be compared besides = and !=
    betweenOperator = new BetweenOperator("12", "10", "20");
    Assertions.assertFalse((Boolean) betweenOperator.evaluate(null), "Failed on numeric tests");

    Assertions.assertNotEquals(betweenOperator, this);
  }
}
