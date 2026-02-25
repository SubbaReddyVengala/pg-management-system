package com.pg.report.exception;

import feign.FeignException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of("SERVICE_UNAVAILABLE",
                        "One or more upstream services are unavailable: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", ex.getMessage()));
    }

    @Data @Builder
    public static class ErrorResponse {
        private String        errorCode;
        private String        message;
        private LocalDateTime timestamp = LocalDateTime.now();

        public static ErrorResponse of(String code, String msg) {
            return ErrorResponse.builder().errorCode(code).message(msg).build();
        }
    }
}
