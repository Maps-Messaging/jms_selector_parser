/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.mapsmessaging.selector.resolvers;

import io.mapsmessaging.selector.IdentifierMutator;

import java.util.Map;

class MapEvaluator extends IdentifierMutator {

  private final Map<String, Object> map;

  public MapEvaluator(Map<String, Object> map) {
    this.map = map;
  }

  @Override
  public Object get(String key) {
    return map.get(key);
  }

  @Override
  public Object remove(String key) {
    return map.remove(key) != null;
  }

  @Override
  public Object set(String key, Object value) {
    map.put(key, value);
    return true;
  }
}
