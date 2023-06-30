package io.shirohoo.realworld.application.config;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionHandleInterceptor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handle(IllegalArgumentException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handle(NoSuchElementException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handle(AccessDeniedException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handle(AuthenticationException e) {
        log.info(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact the administrator.");
    }
}
