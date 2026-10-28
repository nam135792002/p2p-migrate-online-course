package com.p2p.common.constants;

/**
 * Defines header and MDC keys used to associate logging data with requests and users.
 *
 * <p>This utility class is not intended to be instantiated.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public final class LoggingConstant {

  private LoggingConstant() {
    // prevent installation
  }

  /** HTTP header name used to propagate a correlation identifier. */
  public static final String CORRELATION_ID_HEADER = "P2P-X-Correlation_Id";

  /** MDC key used to store the trace identifier for logging. */
  public static final String MDC_TRACE_ID = "traceId";

  /** MDC key used to store the user identifier for logging. */
  public static final String MDC_USER_ID = "userId";
}
