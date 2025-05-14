/*
 *
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *  Copyright [ 2024 - 2025.  ] [Maps Messaging B.V.]
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package io.mapsmessaging.selector;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Message extends IdentifierMutator {

  @Getter
  private Map<String, Object> map;
  private byte[] opaqueData;

  public Message() {
    map = new LinkedHashMap<>();
    opaqueData = null;
  }

  @Override
  public Object get(String key) {
    if (map != null) {
      return map.get(key);
    }
    return null;
  }

  @Override
  public Object remove(String key) {
    if (map != null) {
      return map.remove(key);
    }
    return null;
  }

  @Override
  public Object set(String key, Object value) {
    if (map != null) {
      return map.put(key, value);
    }
    return null;
  }

  @Override
  public byte[] getOpaqueData() {
    return opaqueData;
  }

  public void setOpaqueData(byte[] opaqueData) {
    this.opaqueData = opaqueData;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }
}
