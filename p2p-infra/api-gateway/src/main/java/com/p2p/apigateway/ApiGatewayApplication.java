package com.p2p.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the API Gateway Spring Boot application.
 *
 * <p>Enables Spring Boot auto-configuration for the gateway.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-27
 */
@SpringBootApplication
public class ApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }
}
