package com.pg.auth.exception;
// Throw this for business rule violations:
//   - Room is full
//   - Overpayment attempt
//   - Tenant already has a profile
//   - Checkout of non-active tenant
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

