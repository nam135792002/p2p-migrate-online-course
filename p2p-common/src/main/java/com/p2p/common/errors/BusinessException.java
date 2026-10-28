package com.p2p.common.errors;

import lombok.Getter;

/**
 * Base runtime exception for business errors associated with an {@link ErrorCode}.
 *
 * <p>Subclasses provide protected constructors to attach an error code and an optional cause.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
@Getter
public class BusinessException extends RuntimeException {

  /** The code describing the type of business error. */
  private final ErrorCode errorCode;

  /**
   * Creates a business exception with an error code and detail message.
   *
   * @param errorCode the code describing the business error
   * @param message the detail message for the exception
   */
  protected BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Creates a business exception with an error code, detail message, and underlying cause.
   *
   * @param errorCode the code describing the business error
   * @param message the detail message for the exception
   * @param cause the underlying cause of the exception
   */
  protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
}
