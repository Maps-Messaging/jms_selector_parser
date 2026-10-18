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
package io.mapsmessaging.selector.actions;

import io.mapsmessaging.selector.IdentifierMutator;
import io.mapsmessaging.selector.IdentifierResolver;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionResolutionTest {

  private static final class MutableValues extends IdentifierMutator {
    private final Map<String, Object> values = new HashMap<>();

    @Override
    public Object get(String key) {
      return values.get(key);
    }

    @Override
    public Object set(String key, Object value) {
      return values.put(key, value);
    }

    @Override
    public Object remove(String key) {
      return values.remove(key);
    }
  }

  @Test
  void setAndRemoveEvaluateAgainstMutableResolver() throws Exception {
    MutableValues values = new MutableValues();
    SetAction set = new SetAction("reading", 42);

    assertSame(set, set.compile());
    assertEquals(null, set.evaluate(values));
    assertEquals(42, values.get("reading"));
    assertEquals(42, new RemoveAction("reading").evaluate(values));
    assertEquals(null, values.get("reading"));

    values.set("7", "indexed");
    assertEquals("indexed", new RemoveAction(7).evaluate(values));
  }

  @Test
  void actionsReturnFalseWithoutMutableTargetOrResolvedKey() throws Exception {
    MutableValues values = new MutableValues();
    IdentifierResolver readOnly = values::get;

    assertEquals(false, new SetAction("reading", 42).evaluate(readOnly));
    assertEquals(false, new RemoveAction("reading").evaluate(readOnly));
    assertEquals(false, new SetAction(null, 42).evaluate(values));
    assertEquals(false, new RemoveAction(null).evaluate(values));
    assertEquals(null, values.get("reading"));
  }

  @Test
  void actionIdentityUsesTheTargetKey() {
    SetAction set = new SetAction("reading", 42);
    SetAction sameKey = new SetAction("reading", 43);
    RemoveAction remove = new RemoveAction("reading");

    assertEquals(set, sameKey);
    assertEquals(set.hashCode(), sameKey.hashCode());
    assertNotEquals(set, new SetAction("other", 42));
    assertFalse(set.equals("reading"));
    assertTrue(set.toString().contains("reading"));

    assertSame(remove, remove.compile());
    assertEquals(remove, new RemoveAction("reading"));
    assertEquals(remove.hashCode(), new RemoveAction("reading").hashCode());
    assertNotEquals(remove, new RemoveAction("other"));
    assertFalse(remove.equals("reading"));
    assertTrue(remove.toString().contains("reading"));
  }
}
