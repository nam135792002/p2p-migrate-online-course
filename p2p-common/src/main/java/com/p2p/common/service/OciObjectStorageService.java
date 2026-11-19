package com.p2p.common.service;

import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.HeadObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.p2p.common.configs.OciStorageProperties;
import com.p2p.common.errors.ErrorCode;
import com.p2p.common.errors.OciObjectStorageException;
import com.p2p.common.errors.ValidationException;
import com.p2p.common.utils.StringUtils;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Provides common operations for objects stored in OCI Object Storage.
 *
 * <p>This service uses the shared Object Storage client and bucket configuration created by the
 * application configuration. The consuming application remains responsible for authorization,
 * metadata persistence, and business validation.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-19
 */
@Service
public class OciObjectStorageService {

  private final ObjectStorage objectStorage;
  private final String namespace;
  private final String bucket;

  /**
   * Creates an OCI Object Storage service from the shared client and application properties.
   *
   * @param objectStorage shared OCI Object Storage client
   * @param properties OCI Object Storage namespace and bucket configuration
   */
  public OciObjectStorageService(
      ObjectStorage objectStorage, OciStorageProperties properties) {
    this.objectStorage =
        Objects.requireNonNull(objectStorage, "objectStorage must not be null");
    Objects.requireNonNull(properties, "properties must not be null");

    this.namespace = requireText(properties.namespace(), "namespace");
    this.bucket = requireText(properties.bucket(), "bucket");
  }

  /**
   * Uploads an object and overwrites an existing object with the same key.
   *
   * @param objectKey key of the object to upload
   * @param inputStream object content; the caller retains ownership of this stream
   * @param contentLength number of bytes in the content
   * @param contentType MIME type of the content
   * @throws ValidationException if an argument is blank or invalid
   * @throws OciObjectStorageException if OCI Object Storage rejects the upload
   */
  public void uploadObject(
      String objectKey, InputStream inputStream, long contentLength, String contentType) {
    // Validate input
    requireText(objectKey, "objectKey");
    if (StringUtils.isNull(inputStream)) {
      throw new ValidationException(
          ErrorCode.VALIDATION_FAILED, "inputStream must not be null");
    }
    if (contentLength < 0) {
      throw new ValidationException(
          ErrorCode.VALIDATION_FAILED,
          "contentLength must be greater than or equal to zero");
    }
    requireText(contentType, "contentType");

    // Update media on oci server
    PutObjectRequest request =
        PutObjectRequest.builder()
            .namespaceName(namespace)
            .bucketName(bucket)
            .objectName(objectKey)
            .putObjectBody(inputStream)
            .contentLength(contentLength)
            .contentType(contentType)
            .build();

    try {
      objectStorage.putObject(request);
    } catch (BmcException exception) {
      throw new OciObjectStorageException(
          ErrorCode.SERVICE_UNAVAILABLE,
          "Unable to upload object to OCI Object Storage",
          exception);
    }
  }

  /**
   * Downloads an object as a stream.
   *
   * @param objectKey key of the object to download
   * @return the object content stream
   * @throws ValidationException if the object key is blank or invalid
   * @throws OciObjectStorageException if OCI Object Storage cannot return the object
   */
  public InputStream downloadObject(String objectKey) {
    requireText(objectKey, "objectKey");

    try {
      GetObjectResponse response =
          objectStorage.getObject(
              GetObjectRequest.builder()
                  .namespaceName(namespace)
                  .bucketName(bucket)
                  .objectName(objectKey)
                  .build());
      return response.getInputStream();
    } catch (BmcException exception) {
      ErrorCode errorCode =
          exception.getStatusCode() == 404
              ? ErrorCode.RESOURCE_NOT_FOUND
              : ErrorCode.SERVICE_UNAVAILABLE;
      throw new OciObjectStorageException(
          errorCode, "Unable to download object from OCI Object Storage", exception);
    }
  }

  /**
   * Deletes an object.
   *
   * <p>A missing object is treated as a successful deletion.
   *
   * @param objectKey key of the object to delete
   * @throws ValidationException if the object key is blank or invalid
   * @throws OciObjectStorageException if OCI Object Storage cannot delete the object
   */
  public void deleteObject(String objectKey) {
    requireText(objectKey, "objectKey");
    try {
      objectStorage.deleteObject(
          DeleteObjectRequest.builder()
              .namespaceName(namespace)
              .bucketName(bucket)
              .objectName(objectKey)
              .build());
    } catch (BmcException exception) {
      if (exception.getStatusCode() != 404) {
        throw new OciObjectStorageException(
            ErrorCode.SERVICE_UNAVAILABLE,
            "Unable to delete object from OCI Object Storage",
            exception);
      }
    }
  }

  /**
   * Checks whether an object exists in the configured bucket.
   *
   * @param objectKey key of the object to check
   * @return {@code true} if the object exists; {@code false} if OCI returns status 404
   * @throws ValidationException if the object key is blank or invalid
   * @throws OciObjectStorageException if OCI Object Storage cannot check the object
   */
  public boolean objectExists(String objectKey) {
    requireText(objectKey, "objectKey");
    try {
      objectStorage.headObject(
          HeadObjectRequest.builder()
              .namespaceName(namespace)
              .bucketName(bucket)
              .objectName(objectKey)
              .build());
      return true;
    } catch (BmcException exception) {
      if (exception.getStatusCode() == 404) {
        return false;
      }
      throw new OciObjectStorageException(
          ErrorCode.SERVICE_UNAVAILABLE,
          "Unable to check object existence in OCI Object Storage",
          exception);
    }
  }

  private static String requireText(String value, String fieldName) {
    if (!StringUtils.isBlank(value)) {
      throw new ValidationException(
          ErrorCode.VALIDATION_FAILED, 
          MessageFormat.format("{0} must not be blank", fieldName)
        );
    }
    return value;
  }
}
