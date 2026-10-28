package com.p2p.common.errors;

/**
 * Represents a business exception raised when an operation is not permitted.
 *
 * <p>The exception always uses {@link ErrorCode#FORBIDDEN}.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public class ForbiddenException extends BusinessException {

  /**
   * Creates a forbidden exception with the specified message.
   *
   * @param errorCode an unused legacy parameter; the error code is always {@link
   *     ErrorCode#FORBIDDEN}
   * @param message the detail message for the exception
   */
  public ForbiddenException(ErrorCode errorCode, String message) {
    super(ErrorCode.FORBIDDEN, message);
  }
}
