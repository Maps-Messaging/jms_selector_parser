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

import io.mapsmessaging.selector.Identifier;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.extensions.ParserExtension;
import io.mapsmessaging.selector.operators.FunctionOperator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings({"java:S6548", "javaarchitecture:S7027"}) // yes it is a singleton
public class ParserFactory {

  private static class Holder {
    static final ParserFactory INSTANCE = new ParserFactory();
  }

  public static ParserFactory getInstance() {
    return ParserFactory.Holder.INSTANCE;
  }

  private final ServiceLoader<ParserExtension> knownParsers;

  private ParserFactory() {
    knownParsers = ServiceLoader.load(ParserExtension.class);
  }

  public FunctionOperator loadParser(Object parserName, List<String> arguments)
      throws ParseException {
    for (ParserExtension parser : knownParsers) {
      if (parserName instanceof String) {
        if (parser.getName().equalsIgnoreCase(parserName.toString())) {
          return new ParserProxy(parser.createInstance(arguments));
        }
      } else if (parserName instanceof Identifier identifier) {
        // We need to lazy load on each resolver here
        return new IdentityLoadParser(identifier, arguments);
      }
    }
    return null;
  }

  public Iterator<ParserExtension> getServices() {
    List<ParserExtension> service = new ArrayList<>();
    for (ParserExtension parser : knownParsers) {
      service.add(parser);
    }
    return service.listIterator();
  }
}
