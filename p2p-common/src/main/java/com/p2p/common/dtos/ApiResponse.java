package com.p2p.common.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standard API response containing either result data or error information.
 *
 * <p>Null-valued properties are omitted from its JSON representation.
 *
 * @param <T> the type of the response data
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  /** Indicates whether the request completed successfully. */
  private boolean success;

  /** The result data returned by the API, when available. */
  private T data;

  /** The time associated with this response. */
  private Instant timestamp;

  /** Error information included when the response represents an error. */
  private ErrorResponse errorResponse;
}
