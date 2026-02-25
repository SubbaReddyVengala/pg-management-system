package com.pg.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RevenueReport {

    private Integer    month;
    private Integer    year;
    private BigDecimal totalRentDue;
    private BigDecimal totalCollected;
    private BigDecimal totalOutstanding;
    private int        paidCount;
    private int        partialCount;
    private int        dueCount;
    private double     collectionRatePercent;
}
