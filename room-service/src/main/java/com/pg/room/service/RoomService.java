package com.pg.room.service;

import com.pg.room.dto.CreateRoomRequest;
import com.pg.room.dto.RoomResponse;
import com.pg.room.dto.UpdateRoomRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RoomService {

    RoomResponse createRoom(CreateRoomRequest request);

    Page<RoomResponse> findAll(String status, String roomType, Pageable pageable);

    RoomResponse findById(Long id);

    RoomResponse updateRoom(Long id, UpdateRoomRequest request);

    void deleteRoom(Long id);

    List<RoomResponse> findAvailableRooms();

    // Called internally by Tenant Service via REST
    void updateOccupancy(Long roomId, int delta);
}
