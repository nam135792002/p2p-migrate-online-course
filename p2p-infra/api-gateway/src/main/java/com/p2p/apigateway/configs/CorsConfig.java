package com.p2p.apigateway.configs;

import com.p2p.apigateway.constants.GatewayConstant;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configures Cross-Origin Resource Sharing for the reactive API Gateway.
 *
 * <p>The configuration applies to all paths and allows all origins, methods, and headers,
 * credentials, and a one-hour preflight cache duration.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-17
 */
@Configuration
public class CorsConfig {

  /**
   * Creates the CORS configuration source used by the gateway.
   *
   * @return the CORS configuration source registered for all request paths
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(GatewayConstant.CORS_CONFIG_MAX_AGE);

    UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
    url.registerCorsConfiguration("/**", configuration);
    return url;
  }
}
