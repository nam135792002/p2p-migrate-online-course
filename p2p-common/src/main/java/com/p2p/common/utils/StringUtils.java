package com.p2p.common.utils;

import java.util.Objects;

/**
 * Provides common null-safe checks for string values.
 *
 * <p>All methods are static and this class is not intended to be instantiated.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
public final class StringUtils {

  private StringUtils() {
    // prevent installation
  }

  /**
   * Checks whether the given string is null.
   *
   * @param value the string to check
   * @return {@code true} if the string is null, otherwise {@code false}
   */
  public static boolean isNull(String value) {
    return Objects.isNull(value);
  }

  /**
   * Checks whether the given string is null or empty.
   *
   * @param value the string to check
   * @return {@code true} if the string is null or empty, otherwise {@code false}
   */
  public static boolean isNullOrEmpty(String value) {
    return isNull(value) || value.isEmpty();
  }

  /**
   * Checks whether the given string is null, empty, or blank.
   *
   * @param value the string to check
   * @return {@code true} if the string is null or blank, otherwise {@code false}
   */
  public static boolean isBlank(String value) {
    return isNull(value) || value.isBlank();
  }
}
