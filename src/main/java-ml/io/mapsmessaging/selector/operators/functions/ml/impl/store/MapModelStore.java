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

package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import io.mapsmessaging.selector.operators.functions.ml.ModelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapModelStore implements ModelStore {
  private final Map<String, byte[]> modelStore;

  public MapModelStore() {
    modelStore = new ConcurrentHashMap<>();
  }

  @Override
  public void saveModel(String modelId, byte[] modelData) {
    modelStore.put(modelId, modelData);
  }

  @Override
  public byte[] loadModel(String modelId)  {
    return modelStore.get(modelId);
  }

  @Override
  public boolean modelExists(String modelId)  {
    return modelStore.containsKey(modelId);
  }

  @Override
  public boolean deleteModel(String modelId) {
    return modelStore.remove(modelId) != null;
  }
}
