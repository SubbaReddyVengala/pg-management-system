package com.pg.report.client.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RentRecordResponse {
    private Long       id;
    private Long       tenantId;
    private Long       roomId;
    private Integer    rentMonth;
    private Integer    rentYear;
    private BigDecimal rentAmount;
    private BigDecimal totalPaid;
    private BigDecimal dueAmount;
    private String     status;
}
