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
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package io.mapsmessaging.selector.resolvers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonEvaluatorTest {

  @Test
  void resolvesNestedArraysAndObjects() {
    JsonObject json = JsonParser.parseString("""
        {"matrix":[[11,12],[21,22]],"items":[{"enabled":true,"reading":12.5}]}
        """).getAsJsonObject();
    JsonEvaluator evaluator = new JsonEvaluator(json);

    assertEquals(22, ((Number) evaluator.get("matrix.1.1")).intValue());
    assertEquals(true, evaluator.get("items.0.enabled"));
    assertEquals(12.5, ((Number) evaluator.get("items.0.reading")).doubleValue());
  }

  @Test
  void returnsNullForMissingOrInvalidArrayPaths() {
    JsonObject json = JsonParser.parseString("""
        {"matrix":[[11,12]],"values":[7]}
        """).getAsJsonObject();
    JsonEvaluator evaluator = new JsonEvaluator(json);

    assertNull(evaluator.get("values.0.extra"));
    assertNull(evaluator.get("matrix.0.1.extra"));
    assertNull(evaluator.get("matrix.0"));
    assertNull(evaluator.get("matrix.missing.0"));
    assertNull(evaluator.get("matrix.-1.0"));
    assertNull(evaluator.get("matrix.9.0"));
    assertNull(evaluator.get("missing"));
  }

  @Test
  void resolvesTopLevelJsonPrimitives() {
    JsonObject json = JsonParser.parseString("""
        {"flag":false,"count":42,"label":"ready"}
        """).getAsJsonObject();
    JsonEvaluator evaluator = new JsonEvaluator(json);

    assertEquals(false, evaluator.get("flag"));
    assertEquals(42, ((Number) evaluator.get("count")).intValue());
    assertEquals("ready", evaluator.get("label"));
  }
}
