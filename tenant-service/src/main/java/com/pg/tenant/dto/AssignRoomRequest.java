package com.pg.tenant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoomRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;
}
