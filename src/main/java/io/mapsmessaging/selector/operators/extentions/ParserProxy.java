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

package io.mapsmessaging.selector.operators.extentions;

import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.extensions.ParserExtension;
import io.mapsmessaging.selector.operators.FunctionOperator;

public class ParserProxy extends FunctionOperator {

  private final ParserExtension parser;

  public ParserProxy(ParserExtension parser) {
    this.parser = parser;
  }

  public Object compile() {
    return this;
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) throws ParseException {
    return convertResult(parser.evaluate(resolver));
  }

  @Override
  public String toString() {
    return "Parse (" + parser.toString() + ")";
  }

  @Override
  public boolean equals(Object test) {
    if (test instanceof ParserProxy parserProxy) {
      return parser.equals(parserProxy.parser);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return parser.hashCode();
  }
}
