package com.p2p.apigateway.jwt;

import com.p2p.apigateway.constants.GatewayConstant;
import com.p2p.common.constants.HeaderConstant;
import com.p2p.common.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Applies JWT authentication and forwards authenticated user claims through gateway headers.
 *
 * <p>OPTIONS requests and configured public path prefixes bypass authentication. For other
 * requests, the filter validates a bearer token and writes its subject and role claims to the
 * corresponding request headers before forwarding the exchange.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-17
 */
@Component
public class GatewayJwtAuthenticationFilter implements WebFilter, Ordered {

  /** Signing key used to verify incoming JWTs. */
  private SecretKey key;

  /**
   * Creates the filter and derives its JWT verification key from the configured secret.
   *
   * @param secret the configured secret used to derive the signing key
   */
  @SuppressFBWarnings(
      value = "CT_CONSTRUCTOR_THROW",
      justification = "Invalid JWT secret must fail application startup.")
  public GatewayJwtAuthenticationFilter(@Value("${app.jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Returns the order used to run this filter after highest-precedence filters.
   *
   * @return the filter order, ten positions after highest precedence
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + GatewayConstant.ORDER_OFFSET;
  }

  /**
   * Applies the gateway's JWT authentication rules to the current request.
   *
   * <p>OPTIONS requests and public paths continue without token validation. GET requests have user
   * identity headers removed before forwarding. Other requests require a valid bearer JWT;
   * successful validation forwards the token subject and role as request headers, while missing or
   * invalid tokens receive an unauthorized response.
   *
   * @param exchange the current server web exchange
   * @param chain the remaining web filter chain
   * @return a {@link Mono} that completes when downstream processing or the unauthorized response
   *     completes
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
      return chain.filter(exchange);
    }

    String path = exchange.getRequest().getPath().value();
    if (GatewayConstant.PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)) {
      return chain.filter(exchange);
    }

    if (HttpMethod.GET.equals(exchange.getRequest().getMethod())) {
      return chain.filter(
          exchange
              .mutate()
              .request(
                  r ->
                      r.headers(
                          h -> {
                            h.remove(HeaderConstant.USER_ID_HEADER);
                            h.remove(HeaderConstant.USER_ROLE_HEADER);
                          }))
              .build());
    }

    String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.isNull(header)
        || !header.startsWith(GatewayConstant.AUTHORIZATION_PREFIX_BEARER)) {
      return reject(exchange, GatewayConstant.MESSAGE_JWT_MISSING_BEARER);
    }

    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(key)
              .build()
              .parseSignedClaims(
                  header.substring(GatewayConstant.NUMBER_OF_CHARACTER_PREFIX_BEARER))
              .getPayload();
      String userId = claims.getSubject();
      String role = String.valueOf(claims.get(GatewayConstant.JWT_CLAIM_ROLE));

      ServerWebExchange mutated =
          exchange
              .mutate()
              .request(
                  r ->
                      r.headers(
                          h -> {
                            h.set(HeaderConstant.USER_ID_HEADER, userId);
                            h.set(HeaderConstant.USER_ROLE_HEADER, role);
                          }))
              .build();
      return chain.filter(mutated);
    } catch (JwtException | IllegalArgumentException e) {
      return reject(exchange, GatewayConstant.MESSAGE_JWT_INVALID);
    }
  }

  /**
   * Writes an unauthorized response with the specified message.
   *
   * @param exchange the current server web exchange
   * @param message the message to include in the response body
   * @return a {@link Mono} that completes when the response body has been written
   */
  private Mono<Void> reject(ServerWebExchange exchange, String message) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] body =
        ("{\"code\":\"UNAUTHORIZED\",\"status\":401,\"message\":\"" + message + "\"}")
            .getBytes(StandardCharsets.UTF_8);

    return exchange
        .getResponse()
        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
  }
}
