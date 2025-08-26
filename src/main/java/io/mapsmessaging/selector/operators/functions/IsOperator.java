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

package io.mapsmessaging.selector.operators.functions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.operators.FunctionOperator;

public class IsOperator extends FunctionOperator {

  private final Object lhs;
  private final boolean not;

  public IsOperator(Object lhs, boolean not) {
    this.lhs = lhs;
    this.not = not;
  }

  public Object compile() {
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    Object lookup = evaluate(lhs, resolver);
    boolean isNull = lookup == null;
    if (not) {
      return !isNull;
    }
    return isNull;
  }

  public String toString() {
    if (not) {
      return "(" + lhs.toString() + ") IS NOT NULL";
    } else {
      return "(" + lhs.toString() + ") IS NULL";
    }
  }

  @Override
  public boolean equals(Object test) {
    if (test instanceof IsOperator operator) {
      return (lhs.equals(operator.lhs) && not == operator.not);
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (not) {
      return ~lhs.hashCode();
    }
    return lhs.hashCode();
  }
}
