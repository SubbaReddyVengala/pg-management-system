package com.pg.room.service.impl;

import com.pg.room.dto.CreateRoomRequest;
import com.pg.room.dto.RoomResponse;
import com.pg.room.dto.UpdateRoomRequest;
import com.pg.room.entity.Room;
import com.pg.room.enums.RoomStatus;
import com.pg.room.enums.RoomType;
import com.pg.room.exception.BusinessException;
import com.pg.room.exception.ResourceNotFoundException;
import com.pg.room.repository.RoomRepository;
import com.pg.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        if (roomRepo.existsByRoomNumber(request.getRoomNumber())) {
            throw new BusinessException(
                    "Room number already exists: " + request.getRoomNumber());
        }
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .floor(request.getFloor())
                .roomType(request.getRoomType())
                .maxCapacity(request.getMaxCapacity())
                .monthlyRent(request.getMonthlyRent())
                .description(request.getDescription())
                .build();

        Room saved = roomRepo.save(room);
        log.info("Room created: {} (id={})", saved.getRoomNumber(), saved.getId());
        return RoomResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> findAll(String status, String roomType, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            RoomStatus s = RoomStatus.valueOf(status.toUpperCase());
            return roomRepo.findByStatus(s, pageable).map(RoomResponse::from);
        }
        if (roomType != null && !roomType.isBlank()) {
            RoomType t = RoomType.valueOf(roomType.toUpperCase());
            return roomRepo.findByRoomType(t, pageable).map(RoomResponse::from);
        }
        return roomRepo.findAll(pageable).map(RoomResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse findById(Long id) {
        return RoomResponse.from(getRoomOrThrow(id));
    }

    @Override
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = getRoomOrThrow(id);

        if (request.getFloor()       != null) room.setFloor(request.getFloor());
        if (request.getRoomType()    != null) room.setRoomType(request.getRoomType());
        if (request.getMaxCapacity() != null) room.setMaxCapacity(request.getMaxCapacity());
        if (request.getMonthlyRent() != null) room.setMonthlyRent(request.getMonthlyRent());
        if (request.getStatus()      != null) room.setStatus(request.getStatus());
        if (request.getDescription() != null) room.setDescription(request.getDescription());

        return RoomResponse.from(roomRepo.save(room));
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = getRoomOrThrow(id);
        if (room.getCurrentOccupancy() > 0) {
            throw new BusinessException(
                    "Cannot delete room " + room.getRoomNumber() +
                            " — it currently has " + room.getCurrentOccupancy() + " tenant(s)");
        }
        roomRepo.delete(room);
        log.info("Room deleted: {}", room.getRoomNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> findAvailableRooms() {
        return roomRepo.findAllAvailableWithCapacity()
                .stream()
                .map(RoomResponse::from)
                .toList();
    }

    @Override
    public void updateOccupancy(Long roomId, int delta) {
        Room room = getRoomOrThrow(roomId);
        if (delta > 0) {
            room.incrementOccupancy();
        } else if (delta < 0) {
            room.decrementOccupancy();
        }
        roomRepo.save(room);
        log.info("Occupancy updated for room {} — delta={}, new={}",
                room.getRoomNumber(), delta, room.getCurrentOccupancy());
    }

    // ── Private helper ────────────────────────────────────

    private Room getRoomOrThrow(Long id) {
        return roomRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found with id: " + id));
    }
}
