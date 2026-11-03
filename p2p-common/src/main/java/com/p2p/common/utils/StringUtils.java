package com.p2p.common.utils;

import java.util.Objects;
import java.util.UUID;

/**
 * Provides common null-safe string checks and UUID validation.
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
   * Checks whether the given value is null.
   *
   * @param value the value to check
   * @return {@code true} if the value is null, otherwise {@code false}
   */
  public static boolean isNull(Object value) {
    return Objects.isNull(value);
  }

  /**
   * Checks whether the given string is null or empty.
   *
   * @param value the string to check
   * @return {@code true} if the string is null or has no characters, otherwise {@code false}
   */
  public static boolean isNullOrEmpty(String value) {
    return isNull(value) || value.isEmpty();
  }

  /**
   * Checks whether the given string is null or blank.
   *
   * @param value the string to check
   * @return {@code true} if the string is null or contains only whitespace, otherwise {@code false}
   */
  public static boolean isBlank(String value) {
    return isNull(value) || value.isBlank();
  }

  /**
   * Checks whether the value can be parsed as a UUID.
   *
   * @param value the string to parse as a UUID
   * @return {@code true} if parsing succeeds, or {@code false} if the value is not a valid UUID
   * @throws NullPointerException if {@code value} is null
   */
  public static boolean isValidUuid(String value) {
    try {
      UUID.fromString(value);
      return true;
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }
}
