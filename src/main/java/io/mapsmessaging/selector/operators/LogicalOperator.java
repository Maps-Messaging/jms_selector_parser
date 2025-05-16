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

public abstract class LogicalOperator extends Operation {

  protected Object lhs;
  protected Object rhs;

  protected LogicalOperator(Object lhs, Object rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  // The JMS specification does understand that at times the result maybe unknown or null
  @SuppressWarnings("java:S2447")
  protected Boolean test(Object value, IdentifierResolver resolver) throws ParseException {
    if (value instanceof Boolean) {
      return (Boolean) value;
    } else if (value instanceof Operation) {
      Object result = evaluate(((Operation) value).evaluate(resolver), resolver);
      if (result instanceof Boolean) {
        return (Boolean) result;
      }
      return null; // Yes this is valid
    }
    return false;
  }
}
