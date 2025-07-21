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
import io.mapsmessaging.selector.validators.NumericValidator;

public abstract class ArithmeticOperator extends ComputableOperator {

  protected Object lhs;
  protected Object rhs;

  protected ArithmeticOperator(Object lhs, Object rhs) throws ParseException {
    if (NumericValidator.isValid(lhs)) {
      this.lhs = lhs;
    } else {
      throw new ParseException("Expected numeric value, received :" + lhs.getClass());
    }

    if (NumericValidator.isValid(rhs)) {
      this.rhs = rhs;
    } else {
      throw new ParseException("Expected numeric value, received :" + rhs.getClass());
    }
  }

  public Object compile() {
    if (lhs instanceof Operation operation) {
      lhs = operation.compile();
    }
    if (rhs instanceof Operation operation) {
      rhs = operation.compile();
    }
    return compile(lhs, rhs);
  }

  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    var lhsNumber = evaluateToNumber(lhs, resolver);
    var rhsNumber = evaluateToNumber(rhs, resolver);
    if (lhsNumber == null || rhsNumber == null) {
      return false;
    }
    if (lhsNumber instanceof Double doubleLhs) {
      return processDouble(doubleLhs, rhsNumber);
    } else {
      return processInteger((Long) lhsNumber, rhsNumber);
    }
  }
}
