package com.p2p.common.errors;

/**
 * Represents a failure while accessing OCI Object Storage.
 *
 * <p>The exception carries an application error code and the original OCI exception as its cause.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-19
 */
public class OciObjectStorageException extends BusinessException {

  /**
   * Creates an OCI Object Storage exception.
   *
   * @param errorCode application error category for the OCI failure
   * @param message detail message for the exception
   * @param cause original OCI exception
   */
  public OciObjectStorageException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
