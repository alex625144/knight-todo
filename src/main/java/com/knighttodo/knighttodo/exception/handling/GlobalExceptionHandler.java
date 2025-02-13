package com.knighttodo.knighttodo.exception.handling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Object> handleCustomRuntimeException(RuntimeException ex, WebRequest request) {
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        String msg = cause.getMessage();
        ApiErrorResponse errorResponse = createErrorResponse(cause, msg);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(),
                HttpStatus.valueOf(errorResponse.getStatus()), request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleCustomException(Exception ex, WebRequest request) {
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        String msg = cause.getMessage();
        ApiErrorResponse errorResponse = createErrorResponse(cause, msg);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(),
                HttpStatus.valueOf(errorResponse.getStatus()), request);
    }

    private <T> ApiErrorResponse createErrorResponse(T objects, String message) {
        ResponseStatus responseStatus = objects.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (Objects.nonNull(responseStatus)) {
            httpStatus = responseStatus.value();
        }

        return new ApiErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }

}
