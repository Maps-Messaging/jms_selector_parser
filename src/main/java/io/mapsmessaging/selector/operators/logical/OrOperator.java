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

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.LogicalOperator;
import io.mapsmessaging.selector.operators.Operation;

public class OrOperator extends LogicalOperator {

  public OrOperator(Object lhs, Object rhs) {
    super(lhs, rhs);
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lhsValue = evaluate(lhs, resolver);

    if (lhsValue instanceof Boolean lhsBoolean && lhsBoolean) {
      return true;
    }

    Object rhsValue = evaluate(rhs, resolver);
    if (!(rhsValue instanceof Boolean rhsBoolean)) {
      return false;
    }

    return rhsBoolean;
  }

  @Override
  public Object compile() {
    Object compiledLeftHandSide = compile(lhs);
    Object compiledRightHandSide = compile(rhs);

    if (Boolean.TRUE.equals(compiledLeftHandSide) || Boolean.TRUE.equals(compiledRightHandSide)) {
      return true;
    }

    if (Boolean.FALSE.equals(compiledLeftHandSide)) {
      return compiledRightHandSide;
    }

    if (Boolean.FALSE.equals(compiledRightHandSide)) {
      return compiledLeftHandSide;
    }

    if (compiledLeftHandSide == lhs && compiledRightHandSide == rhs) {
      return this;
    }

    return new OrOperator(compiledLeftHandSide, compiledRightHandSide);
  }

  @Override
  public String toString() {
    return "(" + lhs.toString() + ") OR (" + rhs.toString() + ")";
  }

  @Override
  public boolean equals(Object test) {
    if (test instanceof OrOperator operator) {
      return (lhs.equals(operator.lhs) && rhs.equals(operator.rhs));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() ^ rhs.hashCode();
  }
}
