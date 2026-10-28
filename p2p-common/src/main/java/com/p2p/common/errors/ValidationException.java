package com.p2p.common.errors;

/**
 * Represents a business exception raised when request validation fails.
 *
 * <p>The exception always uses {@link ErrorCode#VALIDATION_FAILED}.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public class ValidationException extends BusinessException {

  /**
   * Creates a validation exception with the specified message.
   *
   * @param errorCode an unused legacy parameter; the error code is always
   *     {@link ErrorCode#VALIDATION_FAILED}
   * @param message the detail message for the exception
   */
  public ValidationException(ErrorCode errorCode, String message) {
    super(ErrorCode.VALIDATION_FAILED, message);
  }
}
