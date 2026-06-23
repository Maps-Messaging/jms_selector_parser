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

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IsBetweenParserTest {

  @Test
  void parsesIsNullFalse() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("'value' IS NULL");

    Assertions.assertFalse((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesIsNotNull() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("'value' IS NOT NULL");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesBetween() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("12 BETWEEN 10 AND 20");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesBetweenFalse() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("25 BETWEEN 10 AND 20");

    Assertions.assertFalse((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesNotBetween() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("25 NOT BETWEEN 10 AND 20");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void parsesNotBetweenFalse() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("12 NOT BETWEEN 10 AND 20");

    Assertions.assertFalse((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void betweenAndDoesNotConflictWithLogicalAnd() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("12 BETWEEN 10 AND 20 AND TRUE");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }

  @Test
  void betweenAndLogicalOrPrecedence() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("25 BETWEEN 10 AND 20 OR TRUE");

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(null));
  }
}