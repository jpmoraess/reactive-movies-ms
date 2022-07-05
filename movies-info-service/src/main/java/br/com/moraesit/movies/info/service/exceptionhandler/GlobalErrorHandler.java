package br.com.moraesit.movies.info.service.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException ex) {
        log.error("exception caught in handleRequestBodyError: {}", ex.getMessage(), ex);

        var error = ex.getBindingResult().getAllErrors().stream()
                .map(e -> Objects.requireNonNull(e.getDefaultMessage()))
                .sorted()
                .collect(Collectors.joining(","));

        log.error("Error is: {}", error);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
