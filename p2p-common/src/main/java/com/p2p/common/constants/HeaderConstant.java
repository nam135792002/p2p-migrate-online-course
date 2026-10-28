package com.p2p.common.constants;

/**
 * Defines custom HTTP header names used by the application.
 *
 * <p>This utility class is not intended to be instantiated.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
public final class HeaderConstant {

  private HeaderConstant() {
    // prevent installation
  }

  /** HTTP header name used to propagate a correlation identifier. */
  public static final String CORRELATION_ID_HEADER = "P2P-X-Correlation-Id";

  /** HTTP header name used to specify the preferred request language. */
  public static final String ACCEPT_LANG_HEADER = "P2P-X-Accept-Language";
}
