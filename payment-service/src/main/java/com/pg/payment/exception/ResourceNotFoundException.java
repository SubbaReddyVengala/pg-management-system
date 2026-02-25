package com.pg.payment.exception;
// Throw this when a room / tenant / payment record is not found by ID
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);

    }
}
