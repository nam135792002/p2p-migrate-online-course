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
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.stereotype.Service;

/**
 * Provides upload, download, deletion, and existence-check operations for OCI Object Storage.
 *
 * <p>The service uses the injected OCI client and the namespace and bucket from {@link
 * OciStorageProperties}. OCI SDK failures are exposed as {@link OciObjectStorageException}; missing
 * objects are reported according to the operation's contract.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-19
 */
@Service
public class OciObjectStorageService {

  /** HTTP status returned by OCI when the requested object does not exist. */
  private static final int OCI_NOT_FOUND_STATUS = 404;

  /** OCI client used to execute Object Storage requests. */
  private final ObjectStorage objectStorage;

  /** Namespace containing the configured Object Storage bucket. */
  private final String namespace;

  /** Bucket used for all Object Storage operations in this service. */
  private final String bucket;

  /**
   * Creates a service using the supplied OCI client and storage configuration.
   *
   * @param objectStorage OCI Object Storage client used for requests
   * @param properties configuration containing the namespace and bucket to use
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "OCI ObjectStorage client is injected and shared by Spring")
  public OciObjectStorageService(ObjectStorage objectStorage, OciStorageProperties properties) {
    this.objectStorage = objectStorage;
    this.namespace = properties.namespace();
    this.bucket = properties.bucket();
  }

  /**
   * Uploads the supplied content to the configured bucket.
   *
   * <p>An existing object with the same key is overwritten. Blank object keys or content types,
   * null input streams, and negative content lengths are rejected before the request is sent.
   *
   * @param objectKey key of the object to upload
   * @param inputStream stream containing the object content
   * @param contentLength number of bytes in the content
   * @param contentType MIME type of the content
   * @throws ValidationException if the object key or content type is blank, the input stream is
   *     {@code null}, or the content length is negative
   * @throws OciObjectStorageException if the OCI SDK reports a failure while uploading the object
   */
  public void uploadObject(
      String objectKey, InputStream inputStream, long contentLength, String contentType) {
    // Validate input
    requireText(objectKey, "objectKey");
    if (StringUtils.isNull(inputStream)) {
      throw new ValidationException(ErrorCode.VALIDATION_FAILED, "inputStream must not be null");
    }
    if (contentLength < 0) {
      throw new ValidationException(
          ErrorCode.VALIDATION_FAILED, "contentLength must be greater than or equal to zero");
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
   * Retrieves an object from the configured bucket as a stream.
   *
   * <p>The caller is responsible for closing the returned stream.
   *
   * @param objectKey key of the object to download
   * @return a stream containing the object content
   * @throws ValidationException if the object key is blank
   * @throws OciObjectStorageException if the object is missing or the OCI SDK reports another
   *     failure; a missing object uses {@link ErrorCode#RESOURCE_NOT_FOUND}
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
          exception.getStatusCode() == OCI_NOT_FOUND_STATUS
              ? ErrorCode.RESOURCE_NOT_FOUND
              : ErrorCode.SERVICE_UNAVAILABLE;
      throw new OciObjectStorageException(
          errorCode, "Unable to download object from OCI Object Storage", exception);
    }
  }

  /**
   * Deletes an object from the configured bucket.
   *
   * <p>A 404 response from OCI is treated as a successful deletion. Other OCI SDK failures are
   * wrapped as {@link OciObjectStorageException} with {@link ErrorCode#SERVICE_UNAVAILABLE}.
   *
   * @param objectKey key of the object to delete
   * @throws ValidationException if the object key is blank
   * @throws OciObjectStorageException if OCI reports a failure other than 404
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
      if (exception.getStatusCode() != OCI_NOT_FOUND_STATUS) {
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
   * <p>A 404 response from OCI returns {@code false}. Other OCI SDK failures are wrapped as {@link
   * OciObjectStorageException} with {@link ErrorCode#SERVICE_UNAVAILABLE}.
   *
   * @param objectKey key of the object to check
   * @return {@code true} if OCI confirms the object exists; {@code false} if OCI returns 404
   * @throws ValidationException if the object key is blank
   * @throws OciObjectStorageException if OCI reports a failure other than 404
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
      if (exception.getStatusCode() == OCI_NOT_FOUND_STATUS) {
        return false;
      }
      throw new OciObjectStorageException(
          ErrorCode.SERVICE_UNAVAILABLE,
          "Unable to check object existence in OCI Object Storage",
          exception);
    }
  }

  private static String requireText(String value, String fieldName) {
    if (StringUtils.isBlank(value)) {
      throw new ValidationException(
          ErrorCode.VALIDATION_FAILED, MessageFormat.format("{0} must not be blank", fieldName));
    }
    return value;
  }
}
