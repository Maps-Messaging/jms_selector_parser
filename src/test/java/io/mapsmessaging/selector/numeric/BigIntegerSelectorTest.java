/*
 * Copyright [ 2024 - 2026 ] MapsMessaging B.V.
 *
 * Licensed under the Apache License, Version 2.0 with the Commons Clause
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     https://commonsclause.com/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.mapsmessaging.selector.numeric;

import io.mapsmessaging.selector.Identifier;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.operators.FunctionOperator;
import io.mapsmessaging.selector.operators.ParserExecutor;
import io.mapsmessaging.selector.operators.arithmetic.AddOperator;
import io.mapsmessaging.selector.operators.arithmetic.DivideOperator;
import io.mapsmessaging.selector.operators.arithmetic.MultiplyOperator;
import io.mapsmessaging.selector.operators.arithmetic.NegateOperator;
import io.mapsmessaging.selector.operators.arithmetic.SubtractOperator;
import io.mapsmessaging.selector.operators.comparison.EqualOperator;
import io.mapsmessaging.selector.operators.comparison.GreaterThanOperator;
import io.mapsmessaging.selector.resolvers.MapEvaluator;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BigIntegerSelectorTest {
  private static final BigInteger MAX = new BigInteger("18446744073709551615");
  private static final BigInteger OVER_LONG = BigInteger.ONE.shiftLeft(63);

  @Test
  void identifierReturnsBigIntegerWithoutNarrowing() {
    assertEquals(MAX, new Identifier("value").evaluate(new MapEvaluator(Map.of("value", MAX))));
    assertEquals(42L, new Identifier("value").evaluate(new MapEvaluator(Map.of("value", 42))));
  }

  @Test
  void parserComparesLargeResolverValuesAgainstExistingLongLiterals() throws ParseException {
    MapEvaluator resolver = new MapEvaluator(Map.of("value", MAX));
    for (String selector : new String[]{
        "value > 9223372036854775807",
        "value >= 9223372036854775807",
        "value <> 9223372036854775807",
        "value BETWEEN 1 AND 9223372036854775807"
    }) {
      ParserExecutor parser = SelectorParser.compile(selector);
      assertEquals(!selector.contains("BETWEEN"), parser.evaluate(resolver), selector);
    }
    assertEquals(Boolean.TRUE, SelectorParser.compile("value > 0").evaluate(resolver));
    assertEquals(Boolean.FALSE, SelectorParser.compile("value = 0").evaluate(resolver));
  }

  @Test
  void exactComparisonsHandleOperandOrderAndSignedLong() throws ParseException {
    assertEquals(Boolean.TRUE, new GreaterThanOperator(MAX, Long.MAX_VALUE).evaluate(null));
    assertEquals(Boolean.TRUE, new GreaterThanOperator(0L, BigInteger.valueOf(-1)).evaluate(null));
    assertEquals(Boolean.TRUE, new EqualOperator(BigInteger.valueOf(42), 42L).evaluate(null));
    assertEquals(Boolean.FALSE, new EqualOperator(OVER_LONG, Long.MIN_VALUE).evaluate(null));
  }

  @Test
  void arithmeticIsExactWhenBigIntegerIsPresent() throws ParseException {
    assertEquals(MAX.add(BigInteger.ONE), new AddOperator(MAX, 1L).evaluate(null));
    assertEquals(MAX.subtract(BigInteger.ONE), new SubtractOperator(MAX, 1L).evaluate(null));
    assertEquals(MAX.multiply(BigInteger.TWO), new MultiplyOperator(MAX, 2L).evaluate(null));
    assertEquals(MAX.divide(BigInteger.valueOf(3)), new DivideOperator(MAX, 3L).evaluate(null));
    assertEquals(Double.NaN, new DivideOperator(MAX, 0L).evaluate(null));
    assertEquals(MAX.negate(), new NegateOperator(MAX).evaluate(null));
    assertEquals(13L, new AddOperator(12L, 1L).evaluate(null));
  }

  @Test
  void wideComparisonWithFloatingPointRetainsFloatingBehaviour() {
    assertEquals(Boolean.TRUE, new GreaterThanOperator(MAX, 1.0).evaluate(null));
    assertEquals(Boolean.TRUE, new GreaterThanOperator(1.0, BigInteger.ZERO).evaluate(null));
  }
}
