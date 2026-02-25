package com.pg.payment.dto;

import com.pg.payment.entity.Payment;
import com.pg.payment.enums.PaymentMode;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long          id;
    private Long          rentRecordId;
    private BigDecimal    amountPaid;
    private PaymentMode   paymentMode;
    private String        referenceNumber;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .rentRecordId(p.getRentRecord().getId())
                .amountPaid(p.getAmountPaid())
                .paymentMode(p.getPaymentMode())
                .referenceNumber(p.getReferenceNumber())
                .paymentDate(p.getPaymentDate())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
