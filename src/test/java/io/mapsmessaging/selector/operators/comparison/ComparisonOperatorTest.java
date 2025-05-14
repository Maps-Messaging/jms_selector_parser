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

package io.mapsmessaging.selector.operators.comparison;

import io.mapsmessaging.selector.IdentifierMutator;
import io.mapsmessaging.selector.IdentifierResolver;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.BeforeEach;

public class ComparisonOperatorTest {

  private HashMap<String, Object> dataSet;

  @BeforeEach
  void buildDataSet() {
    dataSet = new LinkedHashMap<>();
    dataSet.put("textKey", "text data");
    dataSet.put("textNumericLongKey", "102");
    dataSet.put("textNumericRealKey", "102.11");
    dataSet.put("longKey", 1002L);
    dataSet.put("intKey", 1002);
    dataSet.put("shortKey", (short) 1002);
    dataSet.put("byteKey", (byte) 0x1f);
    dataSet.put("doubleKey", 1002.12);
    dataSet.put("floatKey", 1002.12f);
  }

  public IdentifierResolver getResolver() {
    return new IdentifierMutator() {

      @Override
      public Object get(String key) {
        return dataSet.get(key);
      }

      @Override
      public Object remove(String key) {
        return dataSet.remove(key);
      }

      @Override
      public Object set(String key, Object value) {
        return dataSet.put(key, value);
      }
    };
  }

}
