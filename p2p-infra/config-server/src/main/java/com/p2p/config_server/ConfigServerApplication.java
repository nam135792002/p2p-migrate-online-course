
package com.p2p.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Main entry point for the Spring Cloud Config Server application.
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    /**
     * Starts the Config Server application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
