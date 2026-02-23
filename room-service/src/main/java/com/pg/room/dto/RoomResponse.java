package com.pg.room.dto;

import com.pg.room.entity.Room;
import com.pg.room.enums.RoomStatus;
import com.pg.room.enums.RoomType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RoomResponse {

    private Long          id;
    private String        roomNumber;
    private Integer       floor;
    private RoomType      roomType;
    private Integer       maxCapacity;
    private Integer       currentOccupancy;
    private Integer       availableSlots;
    private BigDecimal    monthlyRent;
    private RoomStatus    status;
    private String        description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static factory method — converts Entity to DTO
    public static RoomResponse from(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .floor(room.getFloor())
                .roomType(room.getRoomType())
                .maxCapacity(room.getMaxCapacity())
                .currentOccupancy(room.getCurrentOccupancy())
                .availableSlots(room.getMaxCapacity() - room.getCurrentOccupancy())
                .monthlyRent(room.getMonthlyRent())
                .status(room.getStatus())
                .description(room.getDescription())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
