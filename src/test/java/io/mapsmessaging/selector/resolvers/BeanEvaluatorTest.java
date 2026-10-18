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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BeanEvaluatorTest {

  public static class Child {
    private String name;

    public Child(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class Parent {
    private String name = "before";
    private Child child = new Child("nested");

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Child getChild() {
      return child;
    }

    public void setChild(Child child) {
      this.child = child;
    }

    public String getBroken() {
      throw new IllegalStateException("read failed");
    }

    public void setBroken(String value) {
      throw new IllegalStateException("write failed");
    }
  }

  @Test
  void readsSimpleAndNestedBeanProperties() {
    BeanEvaluator evaluator = new BeanEvaluator(new Parent());

    assertEquals("before", evaluator.get("name"));
    assertEquals("nested", evaluator.get("child#name"));
    assertNull(evaluator.get("child#missing"));
    assertNull(evaluator.get("missing#name"));
    assertNull(evaluator.get("broken"));
    assertNull(evaluator.remove("name"));

    // Repeat on a different instance to exercise the cached getter map.
    assertEquals("before", new BeanEvaluator(new Parent()).get("name"));
  }

  @Test
  void writesBeanPropertyAndReturnsNullForUnavailableOrFailingSetters() {
    Parent parent = new Parent();
    BeanEvaluator evaluator = new BeanEvaluator(parent);

    assertNull(evaluator.set("name", "after"));
    assertEquals("after", parent.getName());
    assertNull(evaluator.set("missing", "ignored"));
    assertNull(evaluator.set("broken", "ignored"));
    assertNull(evaluator.set("missing#name", "ignored"));

    // Repeat on a different instance to exercise the cached setter map.
    Parent second = new Parent();
    assertNull(new BeanEvaluator(second).set("name", "second"));
    assertEquals("second", second.getName());
  }
}
