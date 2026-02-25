package com.pg.room.controller;

import com.pg.room.dto.CreateRoomRequest;
import com.pg.room.dto.RoomResponse;
import com.pg.room.dto.UpdateRoomRequest;
import com.pg.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "APIs for managing PG rooms")
public class RoomController {

    private final RoomService roomService;

    // ── Create ───────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new room")
    public ResponseEntity<RoomResponse> create(
            @Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.createRoom(request));
    }

    // ── Read All (paginated) ──────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all rooms — paginated, filterable by status or type")
    public ResponseEntity<Page<RoomResponse>> getAll(
            @RequestParam(defaultValue = "0")            int page,
            @RequestParam(defaultValue = "10")           int size,
            @RequestParam(defaultValue = "roomNumber")   String sortBy,
            @RequestParam(defaultValue = "ASC")          String direction,
            @RequestParam(required = false)              String status,
            @RequestParam(required = false)              String roomType) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sortBy));

        return ResponseEntity.ok(roomService.findAll(status, roomType, pageable));
    }

    // ── Read One ─────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by ID")
    public ResponseEntity<RoomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    // ── Available Rooms ───────────────────────────────────

    @GetMapping("/available")
    @Operation(summary = "Get all rooms with available capacity")
    public ResponseEntity<List<RoomResponse>> getAvailable() {
        return ResponseEntity.ok(roomService.findAvailableRooms());
    }

    // ── Update ───────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room details")
    public ResponseEntity<RoomResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    // ── Delete ───────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a room — only if unoccupied")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // ── Internal endpoint — called by Tenant Service ──────

    @PutMapping("/{id}/occupancy")
    @Operation(summary = "Update occupancy count — internal use by Tenant Service")
    public ResponseEntity<Void> updateOccupancy(
            @PathVariable Long id,
            @RequestParam int delta) {
        roomService.updateOccupancy(id, delta);
        return ResponseEntity.ok().build();
    }
}
