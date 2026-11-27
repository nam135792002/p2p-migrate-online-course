package com.p2p.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Starts the Eureka Discovery Server application.
 *
 * <p>Enables the Eureka server alongside Spring Boot auto-configuration.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-27
 */
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiscoveryServerApplication.class, args);
  }
}
