package com.pg.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TenantDuesReport {

    private int totalDefaulters;
    private BigDecimal totalDueAmount;
    private List<DueEntry> dues;

    @Data
    @Builder
    public static class DueEntry {
        private Long tenantId;
        private Long rentRecordId;
        private Integer rentMonth;
        private Integer rentYear;
        private BigDecimal dueAmount;
        private String status;
    }
}