package com.pg.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OccupancyReport {

    private int             totalRooms;
    private int             totalCapacity;
    private int             totalOccupied;
    private int             totalAvailable;
    private double          occupancyRatePercent;
    private BigDecimal      totalMonthlyRentPotential;
    private List<FloorStat> floorBreakdown;

    @Data
    @Builder
    public static class FloorStat {
        private Integer floor;
        private int     totalRooms;
        private int     occupiedRooms;
        private int     availableRooms;
    }
}
