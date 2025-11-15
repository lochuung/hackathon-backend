package vn.hackathon.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import vn.hackathon.backend.exception.BadRequestException;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

  private final S3Client s3Client;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  @Value("${aws.s3.endpoint:https://s3.vn-hcm-1.vietnix.cloud}")
  private String endpoint;

  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

  public String uploadFile(MultipartFile file, String folder) {
    validateFile(file);

    String fileName = generateFileName(file.getOriginalFilename());
    String key = folder != null ? folder + "/" + fileName : fileName;

    try {
      PutObjectRequest putObjectRequest =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .contentType(file.getContentType())
              .contentLength(file.getSize())
              .build();

      PutObjectResponse response =
          s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

      log.info("File uploaded successfully: {}, ETag: {}", key, response.eTag());

      return key;
    } catch (IOException e) {
      log.error("Error uploading file to S3: {}", e.getMessage(), e);
      throw BadRequestException.message("Failed to upload file: " + e.getMessage());
    }
  }

  public String getFileUrl(String key) {
    return endpoint + "/" + bucketName + "/" + key;
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw BadRequestException.message("File is empty");
    }

    if (file.getSize() > MAX_FILE_SIZE) {
      throw BadRequestException.message(
          "File size exceeds maximum limit of 10MB. Current size: "
              + (file.getSize() / 1024 / 1024)
              + "MB");
    }

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || originalFilename.trim().isEmpty()) {
      throw BadRequestException.message("File name is invalid");
    }
  }

  private String generateFileName(String originalFilename) {
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    return UUID.randomUUID().toString() + extension;
  }
}
