package com.pg.room.repository;

import com.pg.room.entity.Room;
import com.pg.room.enums.RoomStatus;
import com.pg.room.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(String roomNumber);

    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    Page<Room> findByRoomType(RoomType roomType, Pageable pageable);

    Page<Room> findByFloor(Integer floor, Pageable pageable);

    List<Room> findByStatus(RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.currentOccupancy < r.maxCapacity            AND r.status = 'AVAILABLE'")
    List<Room> findAllAvailableWithCapacity();

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = :status")
    long countByStatus(RoomStatus status);
}
