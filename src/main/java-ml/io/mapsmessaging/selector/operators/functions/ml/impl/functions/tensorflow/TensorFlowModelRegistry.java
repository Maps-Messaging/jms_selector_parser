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

package io.mapsmessaging.selector.operators.functions.ml.impl.functions.tensorflow;

import io.mapsmessaging.selector.operators.functions.ml.ModelException;
import io.mapsmessaging.selector.ml.ModelStore;
import io.mapsmessaging.selector.ml.impl.store.ModelUtils;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.tensorflow.SavedModelBundle;

public class TensorFlowModelRegistry {

  private static final Map<String, TensorFlowModelEntry> registry = new ConcurrentHashMap<>();

  public static TensorFlowModelEntry get(String modelName) {
    return registry.get(modelName);
  }

  public static TensorFlowModelEntry getOrLoad(String modelName, ModelStore store) throws ModelException {
    TensorFlowModelEntry cached = registry.get(modelName);
    if (cached != null) return cached;

    // If not cached, load and insert
    TensorFlowModelEntry entry;
    try {
      entry = loadModel(modelName, store);
    } catch (IOException e) {
      throw new ModelException(e);
    }
    // Store only if absent (racing threads safe)
    return registry.computeIfAbsent(modelName, m -> entry);
  }

  private static TensorFlowModelEntry loadModel(String modelName, ModelStore store) throws IOException {
    byte[] modelData = store.loadModel(modelName+".zip" );

    SavedModelBundle model = ModelUtils.byteArrayToModel(modelData, modelName);

    return new TensorFlowModelEntry(model);
  }

  private TensorFlowModelRegistry() {}
}

