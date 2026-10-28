package com.p2p.common.utils;

import com.p2p.common.dtos.ErrorResponse;
import com.p2p.common.errors.BusinessException;
import java.time.Instant;

/**
 * Provides utilities for converting business exceptions into API error response DTOs.
 *
 * <p>This class is not intended to be instantiated.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
public final class ErrorResponseUtil {

  private ErrorResponseUtil() {
    // prevent installation
  }

  /**
   * Creates an error response DTO from a business exception and request context.
   *
   * <p>The error code, message, and HTTP status are taken from the exception. The timestamp is set
   * to the current time.
   *
   * @param ex the business exception to represent
   * @param path the request path associated with the error
   * @param traceId the trace identifier associated with the request
   * @return an error response DTO containing the exception and request information
   */
  public static ErrorResponse createDto(BusinessException ex, String path, String traceId) {
    return ErrorResponse.builder()
        .code(ex.getErrorCode().name())
        .message(ex.getMessage())
        .status(ex.getErrorCode().getHttpStatus().value())
        .path(path)
        .traceId(traceId)
        .timestamp(Instant.now())
        .build();
  }
}
