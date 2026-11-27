package com.p2p.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Starts the Spring Cloud Config Server application.
 *
 * <p>Enables the Config Server alongside Spring Boot auto-configuration.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-27
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConfigServerApplication.class, args);
  }
}
