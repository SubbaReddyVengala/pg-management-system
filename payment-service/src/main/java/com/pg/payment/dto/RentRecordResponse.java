package com.pg.payment.dto;

import com.pg.payment.entity.RentRecord;
import com.pg.payment.enums.RentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RentRecordResponse {

    private Long id;
    private Long tenantId;
    private Long roomId;
    private Integer rentMonth;
    private Integer rentYear;
    private BigDecimal rentAmount;
    private BigDecimal totalPaid;
    private BigDecimal dueAmount;
    private RentStatus status;
    private LocalDateTime createdAt;

    public static RentRecordResponse from(RentRecord r) {
        return RentRecordResponse.builder()
                .id(r.getId())
                .tenantId(r.getTenantId())
                .roomId(r.getRoomId())
                .rentMonth(r.getRentMonth())
                .rentYear(r.getRentYear())
                .rentAmount(r.getRentAmount())
                .totalPaid(r.getTotalPaid())
                .dueAmount(r.getDueAmount())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}