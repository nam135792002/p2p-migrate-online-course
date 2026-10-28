package com.p2p.common.errors;

/**
 * Represents a business exception raised when a requested resource cannot be found.
 *
 * <p>The exception always uses {@link ErrorCode#RESOURCE_NOT_FOUND}.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public class ResourceNotFoundException extends BusinessException {

  /**
   * Creates a resource-not-found exception with the specified message.
   *
   * @param errorCode an unused legacy parameter; the error code is always {@link
   *     ErrorCode#RESOURCE_NOT_FOUND}
   * @param message the detail message for the exception
   */
  public ResourceNotFoundException(ErrorCode errorCode, String message) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message);
  }

  /**
   * Creates a resource-not-found exception with the specified message and cause.
   *
   * @param errorCode an unused legacy parameter; the error code is always {@link
   *     ErrorCode#RESOURCE_NOT_FOUND}
   * @param message the detail message for the exception
   * @param cause the underlying cause of the exception
   */
  public ResourceNotFoundException(ErrorCode errorCode, String message, Throwable cause) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message, cause);
  }
}
