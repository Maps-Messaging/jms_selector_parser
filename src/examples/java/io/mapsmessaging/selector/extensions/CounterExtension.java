/*
 *
 *   Copyright [ 2020 - 2023 ] [Matthew Buckton]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.mapsmessaging.selector.extensions;

import java.util.List;
import io.mapsmessaging.selector.IdentifierResolver;
import java.util.concurrent.atomic.AtomicLong;

public class CounterExtension implements ParserExtension {

  private AtomicLong counter = new AtomicLong(0);

  @Override
  public ParserExtension createInstance(List<String> arguments)  {
    return new CounterExtension();
  }

  @Override
  public Object evaluate(IdentifierResolver resolver) {
    return counter.getAndIncrement();
  }

  @Override
  public String getName() {
    return "counter";
  }

  @Override
  public String getDescription() {
    return "Simple parse counter, increments every call";
  }
}
