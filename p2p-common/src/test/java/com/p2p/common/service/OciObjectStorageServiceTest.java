package com.p2p.common.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Tests validation, request delegation, and OCI error handling for {@link OciObjectStorageService}.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-27
 */
class OciObjectStorageServiceTest {

  /** Mocked OCI client used to isolate the service from network calls. */
  private ObjectStorage objectStorage;

  /** Service instance configured with test namespace and bucket values. */
  private OciObjectStorageService service;

  @BeforeEach
  void setUp() {
    objectStorage = mock(ObjectStorage.class);
    service =
        new OciObjectStorageService(
            objectStorage,
            new OciStorageProperties(
                "ap-singapore-1", "test-namespace", "test-bucket", "test-config", "DEFAULT"));
  }

  @Test
  void uploadObjectRejectsBlankObjectKey() {
    assertThrows(
        ValidationException.class,
        () ->
            service.uploadObject(
                " ", new ByteArrayInputStream(new byte[] {1}), 1, "application/octet-stream"));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void uploadObjectRejectsNullInputStream() {
    assertThrows(
        ValidationException.class,
        () -> service.uploadObject("object-key", null, 0, "application/octet-stream"));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void uploadObjectRejectsNegativeContentLength() {
    assertThrows(
        ValidationException.class,
        () ->
            service.uploadObject(
                "object-key",
                new ByteArrayInputStream(new byte[0]),
                -1,
                "application/octet-stream"));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void uploadObjectRejectsBlankContentType() {
    assertThrows(
        ValidationException.class,
        () -> service.uploadObject("object-key", new ByteArrayInputStream(new byte[] {1}), 1, " "));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void uploadObjectSendsRequestToConfiguredBucket() {
    InputStream content = new ByteArrayInputStream(new byte[] {1, 2});

    service.uploadObject("object-key", content, 2, "application/octet-stream");

    org.mockito.ArgumentCaptor<PutObjectRequest> requestCaptor =
        org.mockito.ArgumentCaptor.forClass(PutObjectRequest.class);
    verify(objectStorage).putObject(requestCaptor.capture());
    PutObjectRequest request = requestCaptor.getValue();
    assertEquals("test-namespace", request.getNamespaceName());
    assertEquals("test-bucket", request.getBucketName());
    assertEquals("object-key", request.getObjectName());
    assertEquals(2L, request.getContentLength());
    assertEquals("application/octet-stream", request.getContentType());
    assertSame(content, request.getPutObjectBody());
  }

  @Test
  void uploadObjectWrapsOciFailure() {
    BmcException cause = bmcException(HttpStatus.BAD_GATEWAY.value());
    when(objectStorage.putObject(any(PutObjectRequest.class))).thenThrow(cause);

    OciObjectStorageException exception =
        assertThrows(
            OciObjectStorageException.class,
            () ->
                service.uploadObject(
                    "object-key",
                    new ByteArrayInputStream(new byte[] {1}),
                    1,
                    "application/octet-stream"));

    assertEquals(ErrorCode.SERVICE_UNAVAILABLE, exception.getErrorCode());
    assertSame(cause, exception.getCause());
  }

  @Test
  void downloadObjectRejectsBlankObjectKey() {
    assertThrows(ValidationException.class, () -> service.downloadObject(" "));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void downloadObjectReturnsContentFromConfiguredBucket() {
    InputStream content = new ByteArrayInputStream(new byte[] {1});
    GetObjectResponse response = mock(GetObjectResponse.class);
    when(response.getInputStream()).thenReturn(content);
    when(objectStorage.getObject(any(GetObjectRequest.class))).thenReturn(response);

    assertSame(content, service.downloadObject("object-key"));

    org.mockito.ArgumentCaptor<GetObjectRequest> requestCaptor =
        org.mockito.ArgumentCaptor.forClass(GetObjectRequest.class);
    verify(objectStorage).getObject(requestCaptor.capture());
    assertEquals("test-namespace", requestCaptor.getValue().getNamespaceName());
    assertEquals("test-bucket", requestCaptor.getValue().getBucketName());
    assertEquals("object-key", requestCaptor.getValue().getObjectName());
  }

  @Test
  void downloadObjectMapsNotFoundToResourceNotFound() {
    BmcException cause = bmcException(HttpStatus.NOT_FOUND.value());
    when(objectStorage.getObject(any(GetObjectRequest.class))).thenThrow(cause);

    OciObjectStorageException exception =
        assertThrows(OciObjectStorageException.class, () -> service.downloadObject("object-key"));

    assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    assertSame(cause, exception.getCause());
  }

  @Test
  void downloadObjectMapsOtherOciFailuresToServiceUnavailable() {
    BmcException cause = bmcException(HttpStatus.BAD_GATEWAY.value());
    when(objectStorage.getObject(any(GetObjectRequest.class))).thenThrow(cause);

    OciObjectStorageException exception =
        assertThrows(OciObjectStorageException.class, () -> service.downloadObject("object-key"));

    assertEquals(ErrorCode.SERVICE_UNAVAILABLE, exception.getErrorCode());
    assertSame(cause, exception.getCause());
  }

  @Test
  void deleteObjectRejectsBlankObjectKey() {
    assertThrows(ValidationException.class, () -> service.deleteObject(" "));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void deleteObjectSendsRequestToConfiguredBucket() {
    service.deleteObject("object-key");

    org.mockito.ArgumentCaptor<DeleteObjectRequest> requestCaptor =
        org.mockito.ArgumentCaptor.forClass(DeleteObjectRequest.class);
    verify(objectStorage).deleteObject(requestCaptor.capture());
    assertEquals("test-namespace", requestCaptor.getValue().getNamespaceName());
    assertEquals("test-bucket", requestCaptor.getValue().getBucketName());
    assertEquals("object-key", requestCaptor.getValue().getObjectName());
  }

  @Test
  void deleteObjectTreatsNotFoundAsSuccessfulDeletion() {
    BmcException cause = bmcException(HttpStatus.NOT_FOUND.value());
    when(objectStorage.deleteObject(any(DeleteObjectRequest.class))).thenThrow(cause);

    assertDoesNotThrow(() -> service.deleteObject("object-key"));
  }

  @Test
  void deleteObjectWrapsOtherOciFailures() {
    BmcException cause = bmcException(HttpStatus.BAD_GATEWAY.value());
    when(objectStorage.deleteObject(any(DeleteObjectRequest.class))).thenThrow(cause);

    OciObjectStorageException exception =
        assertThrows(OciObjectStorageException.class, () -> service.deleteObject("object-key"));

    assertEquals(ErrorCode.SERVICE_UNAVAILABLE, exception.getErrorCode());
    assertSame(cause, exception.getCause());
  }

  @Test
  void objectExistsRejectsBlankObjectKey() {
    assertThrows(ValidationException.class, () -> service.objectExists(" "));

    verifyNoInteractions(objectStorage);
  }

  @Test
  void objectExistsReturnsTrueWhenHeadRequestSucceeds() {
    assertTrue(service.objectExists("object-key"));

    org.mockito.ArgumentCaptor<HeadObjectRequest> requestCaptor =
        org.mockito.ArgumentCaptor.forClass(HeadObjectRequest.class);
    verify(objectStorage).headObject(requestCaptor.capture());
    assertEquals("test-namespace", requestCaptor.getValue().getNamespaceName());
    assertEquals("test-bucket", requestCaptor.getValue().getBucketName());
    assertEquals("object-key", requestCaptor.getValue().getObjectName());
  }

  @Test
  void objectExistsReturnsFalseWhenOciReportsNotFound() {
    BmcException cause = bmcException(HttpStatus.NOT_FOUND.value());
    when(objectStorage.headObject(any(HeadObjectRequest.class))).thenThrow(cause);

    assertFalse(service.objectExists("object-key"));
  }

  @Test
  void objectExistsWrapsOtherOciFailures() {
    BmcException cause = bmcException(HttpStatus.BAD_GATEWAY.value());
    when(objectStorage.headObject(any(HeadObjectRequest.class))).thenThrow(cause);

    OciObjectStorageException exception =
        assertThrows(OciObjectStorageException.class, () -> service.objectExists("object-key"));

    assertEquals(ErrorCode.SERVICE_UNAVAILABLE, exception.getErrorCode());
    assertSame(cause, exception.getCause());
  }

  private BmcException bmcException(int statusCode) {
    BmcException exception = mock(BmcException.class);
    when(exception.getStatusCode()).thenReturn(statusCode);
    return exception;
  }
}
