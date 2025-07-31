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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class S3ModelStore implements ModelStore {

  private static final int PART_SIZE = 5 * 1024 * 1024; // 5MB

  private final S3Client s3;
  protected final String bucket;
  protected final String prefix;
  private final String endpoint;
  protected final String accessKey;
  protected final String secretKey;


  // No overrides uses AWS S3
  public S3ModelStore(String bucket, String prefix, Region region, String accessKey, String secretKey) {
    this(bucket, prefix, region, accessKey, secretKey, null);
  }

  // Allows the endpoint (url) to be changed to support MinIO
  public S3ModelStore(String bucket, String prefix, Region region, String accessKey, String secretKey, String endpoint) {
    this.bucket = bucket;
    this.prefix = (prefix == null || prefix.isEmpty()) ? "" : (prefix.endsWith("/") ? prefix : prefix + "/");
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.endpoint = endpoint;
    this.s3 = buildS3Client(region);
  }

  protected S3Client buildS3Client(Region region) {
    S3ClientBuilder builder = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)));

    if (endpoint != null) {
      builder.endpointOverride(URI.create(endpoint));
    }

    return builder.build();
  }

  protected String resolveKey(String modelId) {
    return prefix + modelId;
  }

  @Override
  public void saveModel(String modelId, byte[] modelData) throws IOException {
    String key = resolveKey(modelId);
    try {
      CreateMultipartUploadResponse createResp = s3.createMultipartUpload(CreateMultipartUploadRequest.builder()
          .bucket(bucket)
          .key(key)
          .contentType(contentType(modelId))
          .build());

      String uploadId = createResp.uploadId();
      List<CompletedPart> completedParts = new ArrayList<>();

      int partNumber = 1;
      for (int offset = 0; offset < modelData.length; offset += PART_SIZE) {
        int size = Math.min(PART_SIZE, modelData.length - offset);
        byte[] part = new byte[size];
        System.arraycopy(modelData, offset, part, 0, size);

        UploadPartResponse partResp = s3.uploadPart(UploadPartRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .contentLength((long) size)
                .build(),
            RequestBody.fromBytes(part));

        completedParts.add(CompletedPart.builder()
            .partNumber(partNumber)
            .eTag(partResp.eTag())
            .build());

        partNumber++;
      }

      s3.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
          .bucket(bucket)
          .key(key)
          .uploadId(uploadId)
          .multipartUpload(CompletedMultipartUpload.builder()
              .parts(completedParts)
              .build())
          .build());

    } catch (S3Exception e) {
      throw new IOException("Failed to multipart upload model: " + modelId, e);
    }
  }

  @Override
  public byte[] loadModel(String modelId) throws IOException {
    String key = resolveKey(modelId);
    try {
      return s3.getObjectAsBytes(GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build()).asByteArray();
    } catch (NoSuchKeyException e) {
      throw new IOException("Model not found: " + modelId, e);
    } catch (S3Exception e) {
      throw new IOException("Failed to load model: " + modelId, e);
    }
  }

  @Override
  public boolean modelExists(String modelId) throws IOException {
    try {
      s3.headObject(HeadObjectRequest.builder()
          .bucket(bucket)
          .key(resolveKey(modelId))
          .build());
      return true;
    } catch (S3Exception e) {
      return false;
    }
  }

  @Override
  public boolean deleteModel(String modelId) throws IOException {
    try {
      s3.deleteObject(DeleteObjectRequest.builder()
          .bucket(bucket)
          .key(resolveKey(modelId))
          .build());
      return true;
    } catch (S3Exception e) {
      return false;
    }
  }

  private String contentType(String modelId) {
    if (modelId.endsWith(".zip")) return "application/zip";
    if (modelId.endsWith(".arff")) return "text/plain";
    if (modelId.endsWith(".pb")) return "application/octet-stream"; // TensorFlow graph
    if (modelId.endsWith(".h5")) return "application/octet-stream"; // Keras model
    if (modelId.endsWith(".tflite")) return "application/octet-stream"; // TF Lite
    if (modelId.endsWith(".onnx")) return "application/octet-stream"; // ONNX model
    return "application/octet-stream";
  }

}

