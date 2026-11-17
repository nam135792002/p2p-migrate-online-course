package com.p2p.apigateway.configs;

import com.p2p.apigateway.constants.GatewayConstant;
import com.p2p.common.utils.StringUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Configures request rate-limiting key resolution for the API Gateway.
 *
 * <p>Requests are keyed by the remote client's IP address, with a shared fallback key when the
 * remote address is unavailable.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-17
 */
@Configuration
public class RateLimitConfig {

  /**
   * Creates a resolver that uses the request's remote IP address as its rate-limit key.
   *
   * @return a resolver that emits the remote IP address or the anonymous fallback key
   */
  @Bean
  public KeyResolver ipKeyResolver() {
    return exchange ->
        Mono.just(
            !StringUtils.isNull(exchange.getRequest().getRemoteAddress())
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : GatewayConstant.IP_ANONYMOUS);
  }
}
