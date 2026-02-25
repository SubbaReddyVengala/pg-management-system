package com.pg.payment.dto;

import com.pg.payment.enums.PaymentMode;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecordPaymentRequest {

    @NotNull(message = "Rent record ID is required")
    private Long rentRecordId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    private String referenceNumber;
}
