package com.pg.tenant.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private String              errorCode;
    private String              message;
    private Map<String, String> fieldErrors;   // only for 400 validation errors

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Convenience factory for simple errors
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .errorCode(code)
                .message(message)
                .build();
    }

    // Convenience factory for validation errors with field-level details
    public static ErrorResponse ofValidation(Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Input validation failed")
                .fieldErrors(fieldErrors)
                .build();
    }
}
