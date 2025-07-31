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

package io.mapsmessaging.selector.ml.impl.store;

import io.mapsmessaging.selector.ml.ModelStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class NexusModelStore implements ModelStore {

  private final String baseUrl;
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private String authHeader;

  public NexusModelStore(String baseUrl) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
  }

  public void login(String username, String password) {
    String encoded = Base64.getEncoder()
        .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    this.authHeader = "Basic " + encoded;
  }

  @Override
  public void saveModel(String modelId, byte[] modelData) throws IOException {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + modelId))
        .header("Content-Type", "application/zip")
        .PUT(HttpRequest.BodyPublishers.ofByteArray(modelData));

    applyAuth(builder);
    sendRequest(builder.build(), "Failed to upload model: " + modelId);
  }

  @Override
  public byte[] loadModel(String modelId) throws IOException {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + modelId))
        .GET();

    applyAuth(builder);
    HttpResponse<byte[]> response = sendByteRequest(builder.build());
    if (response != null && response.statusCode() == 200) return response.body();
    throw new FileNotFoundException("Model not found: " + modelId);
  }

  @Override
  public boolean modelExists(String modelId) throws IOException {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + modelId))
        .method("HEAD", HttpRequest.BodyPublishers.noBody());

    applyAuth(builder);
    HttpResponse<Void> response = sendVoidRequest(builder.build());
    return response != null && response.statusCode() == 200;
  }

  @Override
  public boolean deleteModel(String modelId) throws IOException {
    HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + modelId))
        .DELETE();

    applyAuth(builder);
    HttpResponse<Void> response = sendVoidRequest(builder.build());
    return response != null && response.statusCode() == 204;
  }

  private void applyAuth(HttpRequest.Builder builder) {
    if (authHeader != null) {
      builder.header("Authorization", authHeader);
    }
  }

  private HttpResponse<Void> sendVoidRequest(HttpRequest request) throws IOException {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted during request", e);
    }
  }

  private HttpResponse<byte[]> sendByteRequest(HttpRequest request) throws IOException {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted during request", e);
    }
  }

  private void sendRequest(HttpRequest request, String errorMessage) throws IOException {
    HttpResponse<Void> response = sendVoidRequest(request);
    if (response.statusCode() >= 300) {
      throw new IOException(errorMessage + " (status " + response.statusCode() + ")");
    }
  }
}
