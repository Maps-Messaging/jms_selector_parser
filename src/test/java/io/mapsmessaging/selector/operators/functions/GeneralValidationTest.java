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

package io.mapsmessaging.selector.operators.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.operators.Operation;
import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.logical.AndOperator;
import io.mapsmessaging.selector.operators.logical.OrOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneralValidationTest {
  @Test
  void parsesBetween() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("12 BETWEEN 10 AND 20");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesLike() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("'abc' LIKE 'a%'");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesIn() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("'a' IN ('a', 'b', 'c')");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }
  @Test
  void falseAndDoesNotEvaluateRightHandSide() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("FALSE AND bad = 1");

    Assertions.assertFalse((Boolean) parserExecutor.evaluate(new ThrowingResolver()));
  }

  @Test
  void trueOrDoesNotEvaluateRightHandSide() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("TRUE OR bad = 1");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(new ThrowingResolver()));
  }

  @Test
  void andDoesNotEvaluateRightHandSideWhenLeftIsFalse() throws ParseException {
    AndOperator andOperator = new AndOperator(false, new ThrowingOperation());

    Assertions.assertFalse((Boolean) andOperator.evaluate(null));
  }

  @Test
  void orDoesNotEvaluateRightHandSideWhenLeftIsTrue() throws ParseException {
    OrOperator orOperator = new OrOperator(true, new ThrowingOperation());

    Assertions.assertTrue((Boolean) orOperator.evaluate(null));
  }

  private static class ThrowingOperation extends Operation {

    @Override
    public Object evaluate(IdentifierResolver resolver) throws ParseException {
      throw new ParseException("Right-hand side should not be evaluated");
    }

    @Override
    public Object compile() {
      return this;
    }
  }
}
