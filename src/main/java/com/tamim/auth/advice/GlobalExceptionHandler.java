package com.tamim.auth.advice;

import com.tamim.auth.constant.MessageConstants;
import com.tamim.auth.dto.response.ApiErrorResponse;
import com.tamim.auth.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {

        log.error("Business error: {}", ex.getMessage());

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
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex) {

        log.error("Unexpected system error", ex);

        return ResponseEntity
                .internalServerError()
                .body(
                        ApiErrorResponse.builder()
                                .status(500)
                                .error(MessageConstants.INTERNAL_SERVER_ERROR)
                                .message(MessageConstants.SOMETHING_WENT_WRONG)
                                .errorCode(MessageConstants.INTERNAL_ERROR)
                                .timestamp(Instant.now())
                                .build()
                );
    }

}
