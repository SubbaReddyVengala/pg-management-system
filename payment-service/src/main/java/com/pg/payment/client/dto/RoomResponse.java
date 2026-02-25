package com.pg.payment.client.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomResponse {
    private Long       id;
    private String     roomNumber;
    private BigDecimal monthlyRent;
    private String     status;
}
