package com.p2p.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.p2p.apigateway.constants.GatewayConstant;
import com.p2p.common.constants.HeaderConstant;
import com.p2p.common.dtos.ErrorResponse;
import com.p2p.common.errors.ErrorCode;
import com.p2p.common.utils.StringUtils;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * Converts uncaught reactive gateway exceptions into JSON error responses.
 *
 * <p>{@link ResponseStatusException} instances retain their HTTP status and use their reason when
 * available. Other exceptions produce an internal-server-error response. If JSON serialization
 * fails, a minimal JSON body is written instead.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since 2026-11-17
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

  /** Mapper used to serialize error response bodies. */
  private final ObjectMapper objectMapper;

  /**
   * Creates the handler with the configured JSON object mapper.
   *
   * @param mapper the object mapper used to serialize error responses
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "ObjectMapper is a Spring-managed singleton configured at startup.")
  public GlobalErrorWebExceptionHandler(ObjectMapper mapper) {
    this.objectMapper = mapper;
  }

  /**
   * Handles an uncaught exception and writes its error response to the exchange.
   *
   * @param exchange the current server web exchange
   * @param ex the exception to convert into an error response
   * @return a {@link Mono} that completes when the response body has been written
   */
  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    HttpStatusCode httpStatusCode;
    String code;
    String message;

    if (ex instanceof ResponseStatusException exception) {
      httpStatusCode = exception.getStatusCode();
      HttpStatus httpStatus = HttpStatus.resolve(httpStatusCode.value());

      code = httpStatus.name();
      message =
          !StringUtils.isNull(exception.getReason())
              ? exception.getReason()
              : httpStatus.getReasonPhrase();
    } else {
      httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
      code = ErrorCode.INTERNAL_ERROR.name();
      message =
          !StringUtils.isNull(ex.getMessage())
              ? ex.getMessage()
              : GatewayConstant.DEFAULT_ERROR_MESSAGE;
    }

    String traceId =
        exchange.getResponse().getHeaders().getFirst(HeaderConstant.CORRELATION_ID_HEADER);
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .code(code)
            .message(message)
            .status(httpStatusCode.value())
            .path(exchange.getRequest().getPath().value())
            .traceId(traceId)
            .timestamp(Instant.now())
            .build();

    exchange.getResponse().setStatusCode(httpStatusCode);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    byte[] bytes;
    try {
      bytes = objectMapper.writeValueAsBytes(errorResponse);
    } catch (JsonProcessingException e) {
      bytes =
          ("{\"code\":\"" + code + "\",\"status\":" + httpStatusCode.value() + "}")
              .getBytes(StandardCharsets.UTF_8);
    }
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}
