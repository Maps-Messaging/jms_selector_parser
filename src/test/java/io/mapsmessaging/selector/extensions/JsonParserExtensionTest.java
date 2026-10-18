/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2026 ] MapsMessaging B.V.
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
package io.mapsmessaging.selector.extensions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonParserExtensionTest {

  private static IdentifierResolver payload(String json) {
    return new IdentifierResolver() {
      @Override
      public Object get(String key) {
        return null;
      }

      @Override
      public byte[] getOpaqueData() {
        return json == null ? null : json.getBytes(StandardCharsets.UTF_8);
      }
    };
  }

  @Test
  void resolvesObjectsArraysAndJsonPrimitives() throws Exception {
    String json = """
        {"items":[{"readings":[2,3.5],"enabled":true}],"label":"ready"}
        """;

    assertEquals(3.5, new JsonParserExtension(List.of("items.0.readings.1")).evaluate(payload(json)));
    assertEquals(2L, new JsonParserExtension(List.of("items.0.readings.0")).evaluate(payload(json)));
    assertEquals(true, new JsonParserExtension(List.of("items.0.enabled")).evaluate(payload(json)));
    assertEquals("ready", new JsonParserExtension(List.of("label")).evaluate(payload(json)));
  }

  @Test
  void returnsNullForMissingMalformedAndOverlongPaths() throws Exception {
    String json = """
        {"items":[[3]],"values":[7]}
        """;
    for (String key : List.of(
        "items.0.0.extra", "items.2.0", "items.-1.0", "items.invalid.0",
        "values.0.extra", "items.0", "missing")) {
      assertNull(new JsonParserExtension(List.of(key)).evaluate(payload(json)), key);
    }
    assertNull(new JsonParserExtension(List.of("values.0")).evaluate(payload("not json")));
    assertNull(new JsonParserExtension(List.of("values.0")).evaluate(payload("{}")));
    assertNull(new JsonParserExtension(List.of("values.0")).evaluate(payload("")));
    assertNull(new JsonParserExtension(List.of("values.0")).evaluate(payload(null)));
  }

  @Test
  void validatesArgumentsAndComparesConfiguredPaths() throws Exception {
    JsonParserExtension factory = new JsonParserExtension();
    assertEquals("json", factory.getName());
    assertTrue(factory.getDescription().contains("JSON"));
    assertThrows(ParseException.class, () -> factory.createInstance(List.of()));

    JsonParserExtension first = (JsonParserExtension) factory.createInstance(List.of("items.0.value"));
    JsonParserExtension same = new JsonParserExtension(List.of("items.0.value"));
    JsonParserExtension other = new JsonParserExtension(List.of("items.1.value"));
    assertEquals(first, same);
    assertEquals(first.hashCode(), same.hashCode());
    assertNotEquals(first, other);
    assertFalse(first.equals("items.0.value"));
    assertTrue(first.toString().contains("items"));

    JsonObject json = JsonParser.parseString("{\"items\":[{\"value\":9}]}").getAsJsonObject();
    assertEquals(9L, first.locateObject(json));
    assertNull(factory.locateObject(json));
  }
}
