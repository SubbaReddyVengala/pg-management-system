package com.pg.room.dto;

import com.pg.room.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor must be 0 or above")
    private Integer floor;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Max capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity cannot exceed 20")
    private Integer maxCapacity;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent must be greater than 0")
    private BigDecimal monthlyRent;

    private String description;
}
