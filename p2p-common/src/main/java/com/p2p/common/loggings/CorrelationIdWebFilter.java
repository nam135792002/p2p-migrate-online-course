package com.p2p.common.loggings;

import com.p2p.common.constants.LoggingConstant;
import com.p2p.common.utils.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Propagates a correlation identifier through reactive HTTP requests.
 *
 * <p>The filter reuses a non-blank inbound correlation ID or generates a UUID when none is
 * provided. It sets the identifier on the response and mutated request headers and adds it to the
 * Reactor context under the trace ID key.
 *
 * <p>This filter is registered only for reactive web applications and runs at the highest
 * precedence.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-10-28
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class CorrelationIdWebFilter implements WebFilter, Ordered {

    /**
     * Returns the highest precedence so this filter can process requests early.
     *
     * @return the highest precedence order value
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Propagates the request correlation ID and delegates processing to the remaining filter chain.
     *
     * <p>A blank or missing inbound ID is replaced with a generated UUID. The selected ID is added
     * to the response header, the mutated request header, and the Reactor context.
     *
     * @param exchange the current server web exchange
     * @param chain the remaining web filter chain
     * @return a {@link Mono} that completes when downstream request processing completes
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String inbound =
                exchange.getRequest().getHeaders().getFirst(LoggingConstant.CORRELATION_ID_HEADER);
        String correlationId = StringUtils.isBlank(inbound) ? UUID.randomUUID().toString() : inbound;

        exchange.getResponse().getHeaders().set(LoggingConstant.CORRELATION_ID_HEADER, correlationId);

        ServerWebExchange mutated =
                exchange
                        .mutate()
                        .request(r -> r.header(LoggingConstant.CORRELATION_ID_HEADER, correlationId))
                        .build();

        return chain.filter(mutated).contextWrite(ctw -> ctw.put(LoggingConstant.MDC_TRACE_ID, correlationId));
    }
}
