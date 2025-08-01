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

import io.mapsmessaging.selector.ml.impl.store.S3ModelStore;

import io.mapsmessaging.selector.model.ModelStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.regions.Region;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class S3ModelStoreTest extends BaseModelStoreTest {

  private S3ModelStore modelStore;

  @BeforeAll
  void setup() {
    String accessKey = System.getenv("S3_ACCESS_KEY");
    String secretKey = System.getenv("S3_SECRET_KEY");
    String regionName = System.getenv("S3_REGION");

    assertNotNull(accessKey, "Missing AWS_ACCESS_KEY environment variable");
    assertNotNull(secretKey, "Missing AWS_SECRET_KEY environment variable");
    assertNotNull(regionName, "Missing AWS_REGION environment variable");

    Region region = Region.of(regionName);
    modelStore = new S3ModelStore("modelstoretest", "test",region, accessKey, secretKey);
  }

  @Override
  ModelStore getModelStore() {
    return modelStore;
  }
}