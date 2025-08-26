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

package io.mapsmessaging.selector.ml;

import io.mapsmessaging.selector.model.ModelStore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseModelStoreTest {


  abstract ModelStore getModelStore();

  @Test
  void testPushListDeleteModels() throws Exception {
    ModelStore modelStore = getModelStore();

    removeRevisions(modelStore,"isoTest.arff");
    removeRevisions(modelStore,"sensor_safety_model.zip");


    uploadAndVerify(modelStore, "isoTest.arff");
    uploadAndVerify(modelStore, "sensor_safety_model.zip");
    Thread.sleep(1000); // allow the models to be removed from cache

    List<String> models = modelStore.listModels();
    assertTrue(models.contains("isoTest.arff"));
    assertTrue(models.contains("sensor_safety_model.zip"));

    deleteAndVerify(modelStore, "isoTest.arff");
    deleteAndVerify(modelStore, "sensor_safety_model.zip");
    Thread.sleep(1000); // allow the models to be removed from cache
    models = modelStore.listModels();
    assertFalse(models.contains("isoTest.arff"));
    assertFalse(models.contains("sensor_safety_model.zip"));
  }

  private void removeRevisions(ModelStore store, String name) throws IOException {
    int breakLoop  = 0;
    while(store.modelExists(name)&& breakLoop < 20) {
      store.deleteModel(name);
      breakLoop++;
    }
  }

  private void uploadAndVerify(ModelStore modelStore, String name) throws IOException {
    byte[] content = Files.readAllBytes(Path.of("src/test/resources/" + name));
    modelStore.saveModel(name, content);

    assertTrue(modelStore.modelExists(name));
    byte[] loaded = modelStore.loadModel(name);
    assertArrayEquals(content, loaded);
  }

  private void deleteAndVerify(ModelStore modelStore, String name) throws IOException {
    modelStore.deleteModel(name);
    assertFalse(modelStore.modelExists(name));
  }

}
