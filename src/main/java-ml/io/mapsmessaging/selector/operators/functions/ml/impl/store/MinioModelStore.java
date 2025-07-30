package io.mapsmessaging.selector.operators.functions.ml.impl.store;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class MinioModelStore extends S3ModelStore {

  private final String endpoint;
  private final String accessKey;
  private final String secretKey;

  public MinioModelStore(String bucket, String prefix, String endpoint,
                         String accessKey, String secretKey, Region region) {
    super(bucket, prefix, region);
    this.endpoint = endpoint;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
  }

  @Override
  protected S3Client buildS3Client(Region region) {
    return S3Client.builder()
        .region(region)
        .endpointOverride(URI.create(endpoint)) // e.g. http://localhost:9000
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}
