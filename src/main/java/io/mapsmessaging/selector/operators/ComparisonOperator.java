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

package io.mapsmessaging.selector.operators;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;

/**
 * From the JMS 2.0 Specification
 *
 * <p>String and Boolean comparison is restricted to = and &lt;&gt;. Two strings are equal if and
 * only if they contain the same sequence of characters.
 */
public abstract class ComparisonOperator extends ComputableOperator {

  protected Object lhs;
  protected Object rhs;

  protected ComparisonOperator(Object lhs, Object rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public Object getRHS() {
    return rhs;
  }

  public Object compile() {
    if (lhs instanceof Operation operation) {
      lhs = operation.compile();
    }
    if (rhs instanceof Operation operation) {
      rhs = operation.compile();
    }
    if ((lhs instanceof Number && rhs instanceof Number)
        || (lhs instanceof Boolean && rhs instanceof Boolean)
        || (lhs instanceof String && rhs instanceof String)) {
      return evaluate(lhs, rhs);
    }
    return this;
  }

  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lhsValue = evaluate(lhs, resolver);
    Object rhsValue = evaluate(rhs, resolver);
    return evaluate(lhsValue, rhsValue);
  }

  public Object evaluate(Object lhsValue, Object rhsValue) {
    if (lhsValue instanceof String  lhsString && rhsValue instanceof String rhsString) {
      return compute(lhsString, rhsString);
    }

    if (lhsValue instanceof Boolean lhsBoolean && rhsValue instanceof Boolean rhsBoolean) {
      return compute(lhsBoolean,rhsBoolean);
    }

    if (lhsValue instanceof Number lhsNumber && rhsValue instanceof Number rhsNumber) {
      return processNumber(lhsNumber, rhsNumber);
    }

    if (lhsValue instanceof String lhsString && rhsValue != null) {
      var lhsNumber = parseStringToNumber(lhsString);
      if (lhsNumber != null) {
        return evaluate(lhsNumber, rhsValue);
      }
    } else if (rhsValue instanceof String rhsString && lhsValue != null) {
      var rhsNumber = parseStringToNumber(rhsString);
      if (rhsNumber != null) {
        return evaluate(lhsValue, rhsNumber);
      }
    }
    return false;
  }

  private Object processNumber(Number lhsNumber, Number rhsNumber) {
    return switch (lhsNumber) {
      case Double lhsDouble -> processDouble(lhsDouble, rhsNumber);
      case Float lhsFloat -> processFloat(lhsFloat, rhsNumber);
      case null, default -> processInteger((Long) lhsNumber, rhsNumber);
    };
  }

  // Regardless of the arguments we can not compare strings by default
  @java.lang.SuppressWarnings("squid:S1172")
  protected Boolean compute(String lhs, String rhs) {
    return false;
  }

  // Regardless of the arguments we can not compare boolean by default
  @java.lang.SuppressWarnings("squid:S1172")
  protected Boolean compute(Boolean lhs, Boolean rhs) {
    return false;
  }
}
