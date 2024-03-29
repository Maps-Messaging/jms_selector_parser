/*
 *    Copyright [ 2020 - 2023 ] [Matthew Buckton]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *
 */

package io.mapsmessaging.selector.operators;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.resolvers.EvaluatorFactory;

public class ParserOperationExecutor implements ParserExecutor {

  private final Operation parser;

  public ParserOperationExecutor(Operation parser) {
    this.parser = parser;
  }

  public boolean evaluate(Object obj) {
    try {
      Object result = parser.evaluate(EvaluatorFactory.create(obj));
      if (result instanceof Boolean) {
        return (Boolean) result;
      }
    } catch (ParseException e) {
      // Log this exception
    }
    return false;
  }

  @Override
  public String toString() {
    return parser.toString();
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs instanceof ParserOperationExecutor) {
      return parser.equals(((ParserOperationExecutor) rhs).parser);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return parser.hashCode();
  }

}
