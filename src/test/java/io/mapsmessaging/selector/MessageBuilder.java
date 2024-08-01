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

package io.mapsmessaging.selector;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageBuilder {

  private final Message message;

  public MessageBuilder() {
    message = new Message();
  }

  public IdentifierResolver build() {
    return message;
  }

  public void setOpaqueData(byte[] bytes) {
    message.setOpaqueData(bytes);
  }

  public Map<String, Object> getDataMap() {
    Map<String, Object> map = message.getMap();
    if (map == null) {
      map = new LinkedHashMap<>();
      message.setMap(map);
    }
    return map;
  }

  public void setDataMap(Map<String, Object> map) {
    message.setMap(map);
  }
}
