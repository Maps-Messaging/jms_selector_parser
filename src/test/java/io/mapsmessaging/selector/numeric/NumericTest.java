/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2026 ] MapsMessaging B.V.
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

package io.mapsmessaging.selector.numeric;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.arithmetic.AddOperator;
import io.mapsmessaging.selector.resolvers.MapEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class NumericTest {

  @Test
  void addOperatorAcceptsIntegerValues() throws ParseException {
    AddOperator addOperator = new AddOperator(12, 1);

    Assertions.assertEquals(13L, addOperator.evaluate(null));
  }

  @Test
  void addOperatorAcceptsShortValues() throws ParseException {
    AddOperator addOperator = new AddOperator((short) 12, (short) 1);

    Assertions.assertEquals(13L, addOperator.evaluate(null));
  }

  @Test
  void addOperatorAcceptsByteValues() throws ParseException {
    AddOperator addOperator = new AddOperator((byte) 12, (byte) 1);

    Assertions.assertEquals(13L, addOperator.evaluate(null));
  }

  @Test
  void addOperatorAcceptsFloatValues() throws ParseException {
    AddOperator addOperator = new AddOperator(12.0f, 1.5f);

    Assertions.assertEquals(13.5d, (Double) addOperator.evaluate(null), 0.0001d);
  }

  @Test
  void comparesIntegerResolverValue() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("value > 10");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(new MapEvaluator(Map.of("value", 12))));
  }

  @Test
  void comparesShortResolverValue() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("value > 10");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(new MapEvaluator(Map.of("value", (short) 12))));
  }

  @Test
  void comparesByteResolverValue() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("value > 10");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(new MapEvaluator(Map.of("value", (byte) 12))));
  }

  @Test
  void addsIntegerResolverValue() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("value + 1 = 13");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(new MapEvaluator(Map.of("value", 12))));
  }

}
