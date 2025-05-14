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

package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.resolvers.EvaluatorFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RemoveTest {

  @Test
  void simpleRemoveTest() throws ParseException {
    RemoveAction removeAction = new RemoveAction("remove");
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("remove", "remove".getBytes());
    removeAction.evaluate(EvaluatorFactory.create(map));
    Assertions.assertEquals(0, map.size());
  }
}
