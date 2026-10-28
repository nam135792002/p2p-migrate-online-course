package com.p2p.common.locales;

import com.p2p.common.constants.HeaderConstant;
import com.p2p.common.constants.LocaleConstant;
import com.p2p.common.utils.StringUtils;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

/**
 * Resolves the locale for requests in reactive web applications.
 *
 * <p>The resolver selects Vietnamese when the configured request-language header starts with the
 * Vietnamese language prefix; otherwise, it selects US English.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GlobalLocaleReactiveResolver implements LocaleContextResolver {

  /**
   * Resolves the request locale from the configured request-language header.
   *
   * <p>If the header is absent or does not start with the configured Vietnamese prefix, US English
   * is used.
   *
   * @param exchange the current server web exchange
   * @return the locale context for the selected locale
   */
  @Override
  public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
    String acceptLang =
        exchange.getRequest().getHeaders().getFirst(HeaderConstant.ACCEPT_LANG_HEADER);
    String language = LocaleConstant.LANG_EN_US;

    if (!StringUtils.isNull(acceptLang)) {
      if (acceptLang.startsWith(LocaleConstant.PREFIX_LANG_VI_VN)) {
        language = LocaleConstant.LANG_VI_VN;
      }
    }
    return new SimpleLocaleContext(Locale.forLanguageTag(language));
  }

  /**
   * Does not support changing the locale context for an exchange.
   *
   * @param exchange the current server web exchange
   * @param localeContext the locale context to set
   * @throws UnsupportedOperationException because this resolver does not implement locale changes
   */
  @Override
  public void setLocaleContext(ServerWebExchange exchange, @Nullable LocaleContext localeContext) {
    throw new UnsupportedOperationException("Unimplemented method 'setLocaleContext'");
  }
}
