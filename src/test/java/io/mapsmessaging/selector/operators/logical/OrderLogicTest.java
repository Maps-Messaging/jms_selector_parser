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

package io.mapsmessaging.selector.operators.logical;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderLogicTest {

  @Test
  void andHasHigherPrecedenceThanOr() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("TRUE OR FALSE AND FALSE");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parenthesesOverrideAndOrPrecedence() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("(TRUE OR FALSE) AND FALSE");

    Assertions.assertFalse((Boolean) parserExecutor.evaluate(null));
  }
  @Test
  void notBindsBeforeAndOr() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("NOT TRUE OR TRUE");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }
  @Test
  void notWithParentheses() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("NOT (TRUE OR FALSE)");

    Assertions.assertFalse((Boolean) parserExecutor.evaluate(null));
  }
  @Test
  void chainedOrAndUsesCorrectPrecedence() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("FALSE OR TRUE AND FALSE OR TRUE");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }
}
