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

import io.mapsmessaging.selector.ml.impl.store.NexusModelStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusModelStoreTest extends BaseModelStoreTest {

  private NexusModelStore modelStore;

  @BeforeAll
  void setup() {
    String user = System.getenv("MODEL_USER");
    String pass = System.getenv("MODEL_PASSWORD");
    assertNotNull(user, "Missing model.user system property");
    assertNotNull(pass, "Missing model.pass system property");

    modelStore = new NexusModelStore("https://repository.mapsmessaging.io/repository/maps_ml_store/");
    modelStore.login(user, pass);
  }

  ModelStore getModelStore() {
    return modelStore;
  }

}
