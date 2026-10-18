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

package io.mapsmessaging.selector.resolvers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.mapsmessaging.selector.IdentifierResolver;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.extensions.JsonParserExtension;
import io.mapsmessaging.selector.operators.ParserExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

class JsonResolverTest {

  @Test
  void jsonExtensionDirectArrayPathWithOutOfBoundsIndexReturnsNull() throws ParseException {
    JsonParserExtension extension = new JsonParserExtension(List.of("items.0.name"));

    JsonObject jsonObject = JsonParser.parseString("""
      {
        "items": []
      }
      """).getAsJsonObject();

    Assertions.assertNull(extension.locateObject(jsonObject));
  }

  @Test
  void jsonExtensionArrayPathWithValidIndexReturnsValue() throws ParseException {
    ParserExecutor parserExecutor = SelectorParser.compile("EXTENSION('json', 'items.0.name') = 'sensor'");

    IdentifierResolver resolver = new OpaqueJsonResolver("""
      {
        "items": [
          {
            "name": "sensor"
          }
        ]
      }
      """);

    Assertions.assertTrue((Boolean) parserExecutor.evaluate(resolver));
  }
  @Test
  void jsonArrayPathWithValidIndexReturnsValue() {
    JsonObject jsonObject = JsonParser.parseString("""
      {
        "items": [
          {
            "name": "sensor"
          }
        ]
      }
      """).getAsJsonObject();

    JsonEvaluator jsonEvaluator = new JsonEvaluator(jsonObject);

    Assertions.assertEquals("sensor", jsonEvaluator.get("items.0.name"));
  }

  @Test
  void jsonArrayPathWithNonNumericIndexReturnsNull() {
    JsonObject jsonObject = JsonParser.parseString("""
      {
        "items": [
          {
            "name": "sensor"
          }
        ]
      }
      """).getAsJsonObject();

    JsonEvaluator jsonEvaluator = new JsonEvaluator(jsonObject);

    Assertions.assertNull(jsonEvaluator.get("items.foo.name"));
  }
  @Test
  void jsonArrayPathWithOutOfBoundsIndexReturnsNull() {
    JsonObject jsonObject = JsonParser.parseString("""
      {
        "items": []
      }
      """).getAsJsonObject();

    JsonEvaluator jsonEvaluator = new JsonEvaluator(jsonObject);

    Assertions.assertNull(jsonEvaluator.get("items.0.foo"));
  }

  @Test
  void jsonArrayPathWithoutIndexReturnsNull() {
    JsonObject jsonObject = JsonParser.parseString("""
      {
        "items": []
      }
      """).getAsJsonObject();

    JsonEvaluator jsonEvaluator = new JsonEvaluator(jsonObject);

    Assertions.assertNull(jsonEvaluator.get("items.foo"));
  }

  private static class OpaqueJsonResolver implements IdentifierResolver {

    private final byte[] opaqueData;

    private OpaqueJsonResolver(String json) {
      this.opaqueData = json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object get(String key) {
      return null;
    }

    @Override
    public byte[] getOpaqueData() {
      return opaqueData;
    }

    @Override
    public List<String> getKeys() {
      return Collections.emptyList();
    }
  }
}
