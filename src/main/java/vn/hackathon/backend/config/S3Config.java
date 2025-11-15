package vn.hackathon.backend.config;

import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config {

  @Value("${aws.s3.endpoint:https://s3.vn-hcm-1.vietnix.cloud}")
  private String endpoint;

  @Value("${aws.s3.access-key}")
  private String accessKey;

  @Value("${aws.s3.secret-key}")
  private String secretKey;

  @Value("${aws.s3.region:vn-hcm-1}")
  private String region;

  /**
   * Creates a StaticCredentialsProvider from the configured access and secret keys.
   *
   * <p>The provider wraps AwsBasicCredentials constructed from this class's accessKey and secretKey
   * fields.
   *
   * @return a StaticCredentialsProvider supplying AwsBasicCredentials for S3 clients
   */
  private StaticCredentialsProvider creds() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }

  /**
   * Creates and configures a synchronous S3Client for blocking or short-lived S3 operations.
   *
   * <p>The client is configured with the configured endpoint and region, path-style access (from
   * s3Cfg()), static credentials (from creds()), request checksum calculation set to WHEN_REQUIRED,
   * and client override timeouts (apiCallTimeout = 2 minutes, apiCallAttemptTimeout = 30 seconds).
   * Suitable for metadata operations and small S3 requests.
   *
   * @return a fully configured S3Client
   */
  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.of(region))
        .serviceConfiguration(s3Cfg())
        .credentialsProvider(creds())
        // Avoid sending optional checksum headers that some S3-compatible stores may not accept
        .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
        .overrideConfiguration(
            ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(30))
                .build())
        .build();
  }

  private S3Configuration s3Cfg() {
    return S3Configuration.builder().pathStyleAccessEnabled(true).build();
  }
}
