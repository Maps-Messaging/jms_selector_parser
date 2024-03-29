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

class GreaterComparatorTest extends ComparisonOperatorTest {

  Object[][] SUCCESS_VALUES = {{4l, 3l}, {4l, 2.1}, {3.9, 2l}, {5.0, 2.0}};
  Object[][] FAILURE_VALUES = {{5l, 30l}, {2l, 3.1}, {1.9, 2l}, {2.0, 2.1}, {"42", "2"}, {false, true}, {true, false}, {"2", "3"}, {true, true}, {false, false}};

  @Test
  void simpleValidationTest() throws ParseException {
    for (Object[] values : SUCCESS_VALUES) {
      GreaterThanOperator greaterThanOperator = new GreaterThanOperator(values[0], values[1]);
      Assertions.assertTrue((Boolean) greaterThanOperator.evaluate(null), "Failed on {" + values[0] + "," + values[1] + "}");
    }
    for (Object[] values : FAILURE_VALUES) {
      GreaterThanOperator greaterThanOperator = new GreaterThanOperator(values[0], values[1]);
      Assertions.assertFalse((Boolean) greaterThanOperator.evaluate(null), "Failed on {" + values[0] + "," + values[1] + "}");
    }
  }

  @Test
  void simpleEquality() {
    GreaterThanOperator greaterThanOperator = new GreaterThanOperator(10.0, 10);
    Assertions.assertEquals("(10.0) > (10)", greaterThanOperator.toString());

    GreaterThanOperator greaterThanOperator2 = new GreaterThanOperator(10.0, 10);
    Assertions.assertEquals(greaterThanOperator, greaterThanOperator2);
    Assertions.assertEquals(greaterThanOperator.hashCode(), greaterThanOperator2.hashCode());

    greaterThanOperator2 = new GreaterThanOperator(10.0, 20.2);
    Assertions.assertNotEquals(greaterThanOperator, greaterThanOperator2);
    Assertions.assertNotEquals(greaterThanOperator.hashCode(), greaterThanOperator2.hashCode());
    Assertions.assertNotEquals(greaterThanOperator, this);
  }

  @Test
  void evaluationCheck() throws ParseException {
    GreaterThanOperator operator = new GreaterThanOperator(new Identifier("textKey"), "text data");
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("textKey"), "text 1 data");
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("textNumericLongKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("textNumericLongKey"), 122L);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("textNumericRealKey"), 10.11);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("textNumericRealKey"), 102.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("longKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("longKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("intKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("intKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("shortKey"), 10L);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("shortKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("byteKey"), 0x1);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("byteKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("doubleKey"), 10.12);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("doubleKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);

    operator = new GreaterThanOperator(new Identifier("floatKey"), 10.12f);
    Assertions.assertEquals(operator.evaluate(getResolver()), Boolean.TRUE);
    operator = new GreaterThanOperator(new Identifier("floatKey"), 10200.12);
    Assertions.assertNotEquals(operator.evaluate(getResolver()), Boolean.TRUE);
  }

}
