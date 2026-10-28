package com.p2p.common.errors;

/**
 * Represents a business exception raised when authentication is required or has failed.
 *
 * <p>The exception always uses {@link ErrorCode#UNAUTHORIZED}.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public class UnauthorizedException extends BusinessException {

  /**
   * Creates an unauthorized exception with the specified message.
   *
   * @param errorCode an unused legacy parameter; the error code is always
   *     {@link ErrorCode#UNAUTHORIZED}
   * @param message the detail message for the exception
   */
  public UnauthorizedException(ErrorCode errorCode, String message) {
    super(ErrorCode.UNAUTHORIZED, message);
  }
}
