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

  /** Indicates that the requested resource does not exist. */
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource was not found"),
  /** Indicates that a resource conflicts with an existing resource. */
  DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "A resource with the specified details already exists"),
  /** Indicates that authentication is required to access a resource. */
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource"),
  /** Indicates that the caller is not permitted to perform an action. */
  FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"),
  /** Indicates that the request contains invalid or missing fields. */
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "The request contains invalid or missing fields"),
  /** Indicates that the service is temporarily unavailable. */
  SERVICE_UNAVAILABLE(
      HttpStatus.SERVICE_UNAVAILABLE,
      "The service is temporarily unavailable. Please try again later"),
  /** Indicates that a payment could not be completed. */
  PAYMENT_FAILED(HttpStatus.PAYMENT_REQUIRED, "The payment could not be completed"),
  /** Indicates that an unexpected internal error occurred. */
  INTERNAL_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later");

  /** The HTTP status associated with this error category. */
  private final HttpStatus httpStatus;

  /** The default message associated with this error category. */
  private final String defaultMessage;
}
