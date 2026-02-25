package com.pg.payment.enums;

public enum RentStatus {
    DUE,      // No payment received yet
    PARTIAL,  // Some amount paid but not full rent
    PAID      // Full rent received
}
