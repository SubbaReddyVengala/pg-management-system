package com.pg.room.entity;

import com.pg.room.enums.RoomStatus;
import com.pg.room.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Builder.Default
    @Column(name = "current_occupancy", nullable = false)
    private Integer currentOccupancy = 0;

    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Business helper methods ───────────────────────

    public boolean hasCapacity() {
        return currentOccupancy < maxCapacity;
    }

    public void incrementOccupancy() {
        if (!hasCapacity()) {
            throw new IllegalStateException("Room " + roomNumber + " is at full capacity");
        }
        this.currentOccupancy++;
        if (this.currentOccupancy >= this.maxCapacity) {
            this.status = RoomStatus.OCCUPIED;
        }
    }

    public void decrementOccupancy() {
        if (this.currentOccupancy <= 0) {
            throw new IllegalStateException("Occupancy cannot go below 0");
        }
        this.currentOccupancy--;
        if (this.currentOccupancy < this.maxCapacity) {
            this.status = RoomStatus.AVAILABLE;
        }
    }
}
