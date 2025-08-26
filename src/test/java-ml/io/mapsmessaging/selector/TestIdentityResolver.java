/*
 *
 *  Copyright [ 2020 - 2024 ] Matthew Buckton
 *  Copyright [ 2024 - 2025 ] MapsMessaging B.V.
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

package io.mapsmessaging.selector;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TestIdentityResolver implements IdentifierResolver {

  protected final double co2;
  protected final double temperature;
  protected final double humidity;


  @Override
  public Object get(String key) {
    return switch (key) {
      case "CO₂" -> co2;
      case "temperature" -> temperature;
      case "humidity" -> humidity;
      default -> Double.NaN;
    };
  }

  @Override
  public List<String> getKeys() {
    return List.of("CO₂", "temperature", "humidity");
  }
}
