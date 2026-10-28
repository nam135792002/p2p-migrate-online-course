package com.p2p.common.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Defines standard error categories, their HTTP statuses, and default messages.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource was not found"),
  DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "A resource with the specified details already exists"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"),
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "The request contains invalid or missing fields"),
  SERVICE_UNAVAILABLE(
      HttpStatus.SERVICE_UNAVAILABLE,
      "The service is temporarily unavailable. Please try again later"),
  PAYMENT_FAILED(HttpStatus.PAYMENT_REQUIRED, "The payment could not be completed"),
  INTERNAL_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later");

  /** The HTTP status associated with this error category. */
  private final HttpStatus httpStatus;

  /** The default message associated with this error category. */
  private final String defaultMessage;
}
