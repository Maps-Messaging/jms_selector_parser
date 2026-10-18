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
package io.mapsmessaging.selector.ml.impl.store;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class S3ModelStoreClientTest {

  private static final byte[] MODEL = {1, 2, 3};

  private static final class FakeS3 {
    final List<String> keys = new ArrayList<>();
    final List<Integer> partNumbers = new ArrayList<>();
    final List<String> contentTypes = new ArrayList<>();
    final AtomicReference<String> failingCall = new AtomicReference<>();
    boolean missing;

    S3Client client() {
      return (S3Client) Proxy.newProxyInstance(
          S3Client.class.getClassLoader(), new Class<?>[]{S3Client.class},
          (proxy, method, args) -> {
            String call = method.getName();
            if (call.equals(failingCall.get())) {
              throw S3Exception.builder().message("test failure").statusCode(503).build();
            }
            return switch (call) {
              case "createMultipartUpload" -> {
                CreateMultipartUploadRequest request = (CreateMultipartUploadRequest) args[0];
                keys.add(request.key());
                contentTypes.add(request.contentType());
                yield CreateMultipartUploadResponse.builder().uploadId("upload-1").build();
              }
              case "uploadPart" -> {
                UploadPartRequest request = (UploadPartRequest) args[0];
                partNumbers.add(request.partNumber());
                yield UploadPartResponse.builder().eTag("etag-" + request.partNumber()).build();
              }
              case "completeMultipartUpload" -> {
                CompleteMultipartUploadRequest request = (CompleteMultipartUploadRequest) args[0];
                keys.add(request.key());
                yield CompleteMultipartUploadResponse.builder().build();
              }
              case "headObject" -> {
                HeadObjectRequest request = (HeadObjectRequest) args[0];
                keys.add(request.key());
                if (missing) {
                  throw NoSuchKeyException.builder().message("missing").build();
                }
                yield HeadObjectResponse.builder().build();
              }
              case "getObjectAsBytes" -> {
                GetObjectRequest request = (GetObjectRequest) args[0];
                keys.add(request.key());
                if (missing) {
                  throw NoSuchKeyException.builder().message("missing").build();
                }
                yield ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), MODEL);
              }
              case "deleteObject" -> {
                DeleteObjectRequest request = (DeleteObjectRequest) args[0];
                keys.add(request.key());
                yield DeleteObjectResponse.builder().build();
              }
              case "listObjectsV2" -> ListObjectsV2Response.builder().contents(
                  S3Object.builder().key("models/isoTest.arff").build(),
                  S3Object.builder().key("models/group/temperature.zip").build(),
                  S3Object.builder().key("models/folder/").build()).build();
              case "close" -> null;
              case "toString" -> "fake S3 client";
              default -> throw new AssertionError("Unexpected S3 call: " + call);
            };
          });
    }
  }

  @Test
  void uploadsLoadsListsAndDeletesWithoutNetwork() throws Exception {
    FakeS3 fake = new FakeS3();
    S3ModelStore store = new S3ModelStore("test-bucket", "models", fake.client());

    store.saveModel("isoTest.arff", MODEL);
    assertEquals(List.of(1), fake.partNumbers);
    assertEquals(List.of("text/plain"), fake.contentTypes);
    assertEquals("models/isoTest.arff", fake.keys.getFirst());
    assertArrayEquals(MODEL, store.loadModel("isoTest.arff"));
    assertTrue(store.modelExists("isoTest.arff"));
    assertEquals(List.of("isoTest.arff", "group.temperature.zip"), store.listModels());
    assertTrue(store.deleteModel("isoTest.arff"));

    assertTrue(fake.keys.stream().allMatch("models/isoTest.arff"::equals));
  }

  @Test
  void uploadsMultiplePartsAndUsesZipContentType() throws Exception {
    FakeS3 fake = new FakeS3();
    S3ModelStore store = new S3ModelStore("test-bucket", "", fake.client());

    store.saveModel("group.model.zip", new byte[5 * 1024 * 1024 + 1]);

    assertEquals(List.of(1, 2), fake.partNumbers);
    assertEquals(List.of("application/zip"), fake.contentTypes);
    assertEquals(List.of("group/model.zip", "group/model.zip"), fake.keys);
  }

  @Test
  void handlesMissingModelsAndServiceErrors() throws Exception {
    FakeS3 fake = new FakeS3();
    S3ModelStore store = new S3ModelStore("test-bucket", "models/", fake.client());

    fake.missing = true;
    assertFalse(store.modelExists("missing"));
    assertThrows(IOException.class, () -> store.loadModel("missing"));
    fake.missing = false;

    fake.failingCall.set("createMultipartUpload");
    assertThrows(IOException.class, () -> store.saveModel("broken", MODEL));
    fake.failingCall.set("getObjectAsBytes");
    assertThrows(IOException.class, () -> store.loadModel("broken"));
    fake.failingCall.set("listObjectsV2");
    assertThrows(IOException.class, store::listModels);
    fake.failingCall.set("deleteObject");
    assertFalse(store.deleteModel("broken"));
  }
}
