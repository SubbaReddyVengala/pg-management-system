package com.pg.tenant.client.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomResponse {
    private Long       id;
    private String     roomNumber;
    private Integer    maxCapacity;
    private Integer    currentOccupancy;
    private Integer    availableSlots;
    private BigDecimal monthlyRent;
    private String     status;   // "AVAILABLE", "OCCUPIED", "MAINTENANCE"
}
