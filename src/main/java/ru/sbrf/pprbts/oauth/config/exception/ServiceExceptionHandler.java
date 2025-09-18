package ru.sbrf.pprbts.oauth.config.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.sbrf.pprbts.oauth.config.exception.type.ErrorResponseDto;
import ru.sbrf.pprbts.oauth.config.exception.type.ServiceException;
import ru.sbrf.pprbts.oauth.config.exception.type.TokenExchangeException;
import ru.sbrf.pprbts.oauth.server.core.utilities.Constants;
import ru.sbrf.pprbts.oauth.server.core.utilities.MdcUtils;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ServiceExceptionHandler {

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class,
            IllegalAccessException.class,
            TokenExchangeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleHttpRequestException(Exception ex) {
        return ErrorResponseDto.builder()
                .message(ex.getMessage())
                .traceId(getCurrentTraceId())
                .build();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn("Endpoint not found: {}", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Endpoint not found: %s".formatted(ex.getRequestURL()));
    }

    @ExceptionHandler({
            Exception.class,
            ServiceException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleAllExceptions(Exception ex) {
        log.error("Service unhandled exception:", ex);
        String traceId = getCurrentTraceId();

        return ErrorResponseDto.builder()
                .traceId(traceId)
                .message(ex.getMessage())
                .build();
    }

    private String getCurrentTraceId() {
        return MdcUtils.getValue(Constants.Request.Context.TRACE_ID);
    }
}
