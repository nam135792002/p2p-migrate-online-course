package com.p2p.common.locales;

import com.p2p.common.constants.HeaderConstant;
import com.p2p.common.constants.LocaleConstant;
import com.p2p.common.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Resolves locales for requests in Servlet-based web applications.
 *
 * <p>The configured request-language header selects Vietnamese when its value starts with the
 * Vietnamese prefix; otherwise, the resolver selects US English.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalLocaleResolver implements LocaleResolver {

  /**
   * Resolves the locale from the configured request-language header.
   *
   * <p>If the header is absent or does not start with the configured Vietnamese prefix, US English
   * is used.
   *
   * @param request the current HTTP request
   * @return the locale selected for the request
   */
  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    String acceptLang = request.getHeader(HeaderConstant.ACCEPT_LANG_HEADER);

    String language = LocaleConstant.LANG_EN_US;
    if (!StringUtils.isNull(acceptLang)) {
      if (acceptLang.startsWith(LocaleConstant.PREFIX_LANG_VI_VN)) {
        language = LocaleConstant.LANG_VI_VN;
      }
    }

    return Locale.forLanguageTag(language);
  }

  /**
   * Does not support changing the locale for a request.
   *
   * @param request the current HTTP request
   * @param response the current HTTP response, if available
   * @param locale the locale to set, if available
   * @throws UnsupportedOperationException because this resolver does not implement locale changes
   */
  @Override
  public void setLocale(
      HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
    throw new UnsupportedOperationException("Unimplemented method 'setLocale'");
  }
}
