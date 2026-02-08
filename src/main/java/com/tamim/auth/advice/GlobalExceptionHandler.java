package com.tamim.auth.advice;

import com.tamim.auth.dto.response.ApiErrorResponse;
import com.tamim.auth.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(
                        ApiErrorResponse.builder()
                                .status(ex.getStatus().value())
                                .error(ex.getStatus().getReasonPhrase())
                                .message(ex.getMessage())
                                .errorCode(ex.getErrorCode())
                                .timestamp(Instant.now())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity
                .internalServerError()
                .body(
                        ApiErrorResponse.builder()
                                .status(500)
                                .error("Internal Server Error")
                                .message("Something went wrong")
                                .errorCode("INTERNAL_ERROR")
                                .timestamp(Instant.now())
                                .build()
                );
    }

}
