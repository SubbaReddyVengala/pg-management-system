package com.pg.payment.entity;

import com.pg.payment.enums.RentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rent_records",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"tenant_id", "rent_month", "rent_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "rent_month", nullable = false)
    private Integer rentMonth;

    @Column(name = "rent_year", nullable = false)
    private Integer rentYear;

    @Column(name = "rent_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentAmount;

    @Builder.Default
    @Column(name = "total_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentStatus status = RentStatus.DUE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Business methods ─────────────────────────────

    public BigDecimal getDueAmount() {
        return rentAmount.subtract(totalPaid);
    }

    public void applyPayment(BigDecimal amount) {
        this.totalPaid = this.totalPaid.add(amount);
        if (this.totalPaid.compareTo(this.rentAmount) >= 0) {
            this.status = RentStatus.PAID;
        } else {
            this.status = RentStatus.PARTIAL;
        }
    }
}
