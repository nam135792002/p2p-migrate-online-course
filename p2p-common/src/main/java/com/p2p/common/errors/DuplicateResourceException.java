package com.p2p.common.errors;

/**
 * Represents a business exception raised when a resource conflicts with an existing resource.
 *
 * <p>The exception always uses {@link ErrorCode#DUPLICATE_RESOURCE}.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public class DuplicateResourceException extends BusinessException {

  /**
   * Creates a duplicate-resource exception with the specified message.
   *
   * @param errorCode an unused legacy parameter; the error code is always {@link
   *     ErrorCode#DUPLICATE_RESOURCE}
   * @param message the detail message for the exception
   */
  public DuplicateResourceException(ErrorCode errorCode, String message) {
    super(ErrorCode.DUPLICATE_RESOURCE, message);
  }
}
