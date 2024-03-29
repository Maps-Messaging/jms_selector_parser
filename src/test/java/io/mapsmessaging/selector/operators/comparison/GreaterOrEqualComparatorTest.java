/*
 *
 *   Copyright [ 2020 - 2023 ] [Matthew Buckton]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.mapsmessaging.selector.operators.comparison;

import io.mapsmessaging.selector.Identifier;
import io.mapsmessaging.selector.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GreaterOrEqualComparatorTest extends ComparisonOperatorTest {

  Object[][] SUCCESS_VALUES = {{4l, 3l}, {4l, 2.1}, {3.9, 2l}, {5.0, 2.0}, {2.0, 2.0}, {5L, 5L}, {2L, 2.0}, {2.0, 2}};
  Object[][] FAILURE_VALUES = {{5l, 30l}, {2l, 3.1}, {1.9, 2l}, {2.0, 2.1}, {"42", "2"}, {false, true}, {true, false}, {"2", "3"}, {true, true}, {false, false}};

  @Test
  void simpleValidationTest() throws ParseException {
    for (Object[] values : SUCCESS_VALUES) {
      GreaterOrEqualOperator greaterOrEqualOperator = new GreaterOrEqualOperator(values[0], values[1]);
      Assertions.assertTrue((Boolean) greaterOrEqualOperator.evaluate(null), "Failed on {" + values[0] + "," + values[1] + "}");
    }
    for (Object[] values : FAILURE_VALUES) {
      GreaterOrEqualOperator greaterOrEqualOperator = new GreaterOrEqualOperator(values[0], values[1]);
      Assertions.assertFalse((Boolean) greaterOrEqualOperator.evaluate(null), "Failed on {" + values[0] + "," + values[1] + "}");
    }
  }

  @Test
  void simpleEquality() {
    GreaterOrEqualOperator greaterOrEqualOperator = new GreaterOrEqualOperator(10.0, 10);
    Assertions.assertEquals("(10.0) >= (10)", greaterOrEqualOperator.toString());

    GreaterOrEqualOperator greaterOrEqualOperator2 = new GreaterOrEqualOperator(10.0, 10);
    Assertions.assertEquals(greaterOrEqualOperator, greaterOrEqualOperator2);
    Assertions.assertEquals(greaterOrEqualOperator.hashCode(), greaterOrEqualOperator2.hashCode());

    greaterOrEqualOperator2 = new GreaterOrEqualOperator(10.0, 20.2);
    Assertions.assertNotEquals(greaterOrEqualOperator, greaterOrEqualOperator2);
    Assertions.assertNotEquals(greaterOrEqualOperator.hashCode(), greaterOrEqualOperator2.hashCode());
    Assertions.assertNotEquals(greaterOrEqualOperator, this);
  }

  @Test
  void evaluationCheck() throws ParseException {
    GreaterOrEqualOperator operator = new GreaterOrEqualOperator(new Identifier("textKey"), "text data");
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("textKey"), "text 1 data");
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("textNumericLongKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("textNumericLongKey"), 122L);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("textNumericRealKey"), 10.11);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("textNumericRealKey"), 102.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("longKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("longKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("intKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("intKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("shortKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("shortKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("byteKey"), 0x1);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("byteKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("doubleKey"), 10.12);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("doubleKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterOrEqualOperator(new Identifier("floatKey"), 10.12f);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterOrEqualOperator(new Identifier("floatKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);
  }
}
