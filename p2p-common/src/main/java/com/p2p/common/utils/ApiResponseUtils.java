package com.p2p.common.utils;

import com.p2p.common.dtos.ApiResponse;
import com.p2p.common.dtos.ErrorResponse;
import java.time.Instant;

/**
 * Provides factory methods for creating standard API responses.
 *
 * <p>Each response is assigned the current timestamp when it is created.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
public final class ApiResponseUtils {

  private ApiResponseUtils() {
    // prevent instantiation
  }

  /**
   * Creates a successful API response containing the specified data.
   *
   * @param <T> the type of the response data
   * @param data the data to include in the response
   * @return a successful response containing the data and current timestamp
   */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<T>(true, data, Instant.now(), null);
  }

  /**
   * Creates a failed API response containing the specified error information.
   *
   * @param <T> the declared type of the response data, which is absent in this failure response
   * @param errorResponse the error information to include in the response
   * @return a failed response containing the error information and current timestamp
   */
  public static <T> ApiResponse<T> failure(ErrorResponse errorResponse) {
    return new ApiResponse<T>(false, null, Instant.now(), errorResponse);
  }
}
