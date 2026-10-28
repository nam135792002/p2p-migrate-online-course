package com.p2p.common.utils;

import com.p2p.common.constants.LocaleConstant;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Resolves localized messages from the configured message bundle.
 *
 * <p>Messages are resolved using the current locale when no explicit locale is provided.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
public final class I18nUtil {

  private I18nUtil() {
    // prevent installation
  }

  /**
   * Resolves a localized message using the current locale.
   *
   * @param code the message code to resolve
   * @return the localized message for the current locale
   */
  public static String getMessage(String code) {
    return getMessage(code, null, null);
  }

  /**
   * Resolves a localized message with the given arguments using the current locale.
   *
   * @param code the message code to resolve
   * @param args the arguments used to format the message
   * @return the localized message for the current locale
   */
  public static String getMessage(String code, Object[] args) {
    return getMessage(code, args, null);
  }

  /**
   * Resolves a localized message with the given arguments and locale.
   *
   * @param code the message code to resolve
   * @param args the arguments used to format the message
   * @param locale the locale to use; if null, the current locale is used
   * @return the localized message for the specified or current locale
   */
  public static String getMessage(String code, Object[] args, Locale locale) {
    if (StringUtils.isNull(locale)) {
      locale = LocaleContextHolder.getLocale();
    }

    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(LocaleConstant.PATH_BASENAME_RESPONSE_CODE);
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setDefaultEncoding(LocaleConstant.DEFAULT_ENCODING_UTF8);

    String message = messageSource.getMessage(code, args, locale);
    return message;
  }
}
