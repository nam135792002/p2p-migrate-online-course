package com.p2p.common.loggings;

import com.p2p.common.constants.HeaderConstant;
import com.p2p.common.constants.LoggingConstant;
import com.p2p.common.utils.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter responsible for managing correlation IDs across HTTP requests.
 *
 * <p>Extracts the correlation ID from the incoming request header or generates a new UUID if the
 * header is missing or blank. The ID is stored in the MDC for logging purposes and propagated
 * through the response header.
 *
 * <p>This filter is only activated in Servlet-based web applications.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-27
 */
@Component
@ConditionalOnClass(name = "jakarta.servlet.Filter")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CorrelationIdFilter extends OncePerRequestFilter implements Ordered {

  /**
   * Processes an incoming HTTP request and manages its correlation ID.
   *
   * <p>The correlation ID is retrieved from the request header or generated if absent. It is then
   * stored in the MDC and added to the response header. The MDC entry is removed after request
   * processing to prevent context leakage between requests.
   *
   * @param request the incoming HTTP request
   * @param response the outgoing HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a Servlet-related error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = request.getHeader(HeaderConstant.CORRELATION_ID_HEADER);
    if (StringUtils.isBlank(correlationId) || !StringUtils.isValidUuid(correlationId)) {
      correlationId = UUID.randomUUID().toString();
    } else {
      // Normalize to a canonical UUID representation.
      correlationId = UUID.fromString(correlationId).toString();
    }

    try {
      MDC.put(LoggingConstant.MDC_TRACE_ID, correlationId);
      response.setHeader(HeaderConstant.CORRELATION_ID_HEADER, correlationId);
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(LoggingConstant.MDC_TRACE_ID);
    }
  }

  /**
   * Returns the execution order of this filter.
   *
   * @return the highest precedence to execute this filter early
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
