package com.pg.room.dto;

import com.pg.room.enums.RoomStatus;
import com.pg.room.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateRoomRequest {

    @Min(value = 0, message = "Floor must be 0 or above")
    private Integer floor;

    private RoomType roomType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer maxCapacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Rent must be greater than 0")
    private BigDecimal monthlyRent;

    private RoomStatus status;

    private String description;
}
