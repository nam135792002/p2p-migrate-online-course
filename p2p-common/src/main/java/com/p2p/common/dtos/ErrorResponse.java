package com.p2p.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents error information returned by the API.
 *
 * <p>Null-valued properties are omitted from its JSON representation.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  /** The code identifying the type of error. */
  private String code;

  /** A human-readable description of the error. */
  private String message;

  /** The HTTP status code associated with the error. */
  private int status;

  /** The request path associated with the error. */
  private String path;

  /** The trace identifier associated with the request, when available. */
  private String traceId;

  /** The time associated with the error response. */
  private Instant timestamp;

  /** Additional error-specific information. */
  private Map<String, Object> details;
}
