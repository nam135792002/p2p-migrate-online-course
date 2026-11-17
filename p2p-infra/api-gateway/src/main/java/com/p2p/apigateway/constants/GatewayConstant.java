package com.p2p.apigateway.constants;

import java.util.List;

/**
 * Defines shared constants used by the API Gateway.
 *
 * <p>This utility class is not intended to be instantiated.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-17
 */
public final class GatewayConstant {

  /** Prevents instantiation of this utility class. */
  private GatewayConstant() {}

  /** Fallback message used when an unexpected error has no message. */
  public static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";

  /** Rate-limit key used when the client's remote IP address is unavailable. */
  public static final String IP_ANONYMOUS = "anonymous";

  /** Path prefixes excluded from JWT authentication by the gateway filter. */
  public static final List<String> PUBLIC_PREFIXES =
      List.of("/api/auth/", "/actuator/", "/v3/api-docs", "/swagger-ui");

  /** Authorization header prefix expected for JWT bearer tokens. */
  public static final String AUTHORIZATION_PREFIX_BEARER = "Bearer ";

  /** Number of characters in the bearer-token prefix. */
  public static final int NUMBER_OF_CHARACTER_PREFIX_BEARER = 7;

  /** JWT claim name containing the user's role. */
  public static final String JWT_CLAIM_ROLE = "role";

  /** Message returned when a required bearer token is missing or malformed. */
  public static final String MESSAGE_JWT_MISSING_BEARER = "Missing Bearer token";

  /** Message returned when a bearer token cannot be validated. */
  public static final String MESSAGE_JWT_INVALID = "Invalid or expired token";
}
