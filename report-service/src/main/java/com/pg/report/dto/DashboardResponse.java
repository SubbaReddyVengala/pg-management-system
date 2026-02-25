package com.pg.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {

    // Room stats
    private int        totalRooms;
    private int        availableRooms;
    private int        occupiedRooms;
    private int        maintenanceRooms;

    // Tenant stats
    private int        totalActiveTenants;
    private int        pendingTenants;

    // Payment stats (current month)
    private BigDecimal currentMonthCollection;
    private int        outstandingDuesCount;
    private BigDecimal totalOutstandingAmount;

    // Occupancy rate
    private double     occupancyRatePercent;
}
