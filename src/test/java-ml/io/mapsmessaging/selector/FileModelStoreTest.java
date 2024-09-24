/*
 *  Copyright [ 2020 - 2024 ] [Matthew Buckton]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.mapsmessaging.selector;

import io.mapsmessaging.selector.operators.functions.ml.impl.store.FileModelStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

class FileModelStoreTest {


  @Test
  void saveAndLoadModel() throws IOException {
    FileModelStore fileModelStore = new FileModelStore(".");
    if(fileModelStore.modelExists("testModel.data")){
      fileModelStore.deleteModel("testModel.data");
    }
    Random random = new Random();
    byte[] testData = new byte[10240];
    random.nextBytes(testData);
    Assertions.assertDoesNotThrow(()->{fileModelStore.saveModel("testModel.data", testData);});
    byte[] reloaded = fileModelStore.loadModel("testModel.data");
    Assertions.assertNotNull(reloaded);
    Assertions.assertArrayEquals(testData, reloaded);
    Assertions.assertDoesNotThrow(()->{fileModelStore.deleteModel("testModel.data");});
  }

  @Test
  void saveAndLoadExceptionsModel() throws IOException {
    FileModelStore fileModelStore = new FileModelStore(".");
    if(fileModelStore.modelExists("testModel.data")){
      fileModelStore.deleteModel("testModel.data");
    }
    Assertions.assertThrows(IOException.class, () ->{ fileModelStore.loadModel("testModel.data");});
  }

}
