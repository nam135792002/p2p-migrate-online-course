package com.p2p.common.configs;

import com.p2p.common.locales.GlobalLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Declares application-level Spring configuration beans.
 *
 * <p>Registers the locale resolver used by Servlet-based request processing.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
@Configuration
public class AppConfig {

  /**
   * Creates the locale resolver bean for handling Servlet request locales.
   *
   * @return the application locale resolver
   */
  @Bean
  public LocaleResolver localeResolver() {
    return new GlobalLocaleResolver();
  }
}
