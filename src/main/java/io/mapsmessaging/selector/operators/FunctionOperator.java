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

public abstract class FunctionOperator extends Operation {

  @Override
  public abstract Object evaluate(IdentifierResolver resolver) throws ParseException;

  protected Object convertResult(Object result) {
    if (result instanceof Number || result instanceof String || result instanceof Boolean) {
      if (result instanceof Double) {
        return result;
      }
      if (result instanceof Float) {
        return ((Float) result).doubleValue();
      } else if (result instanceof Number) {
        return ((Number) result).longValue(); // Forces byte, short, int all to long
      }
      return result;
    }
    return false;
  }
}
