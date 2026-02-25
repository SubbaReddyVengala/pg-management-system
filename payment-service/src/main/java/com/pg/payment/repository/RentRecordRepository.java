package com.pg.payment.repository;

import com.pg.payment.entity.RentRecord;
import com.pg.payment.enums.RentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RentRecordRepository extends JpaRepository<RentRecord, Long> {

    Optional<RentRecord> findByTenantIdAndRentMonthAndRentYear(
            Long tenantId, Integer month, Integer year);

    List<RentRecord> findByStatus(RentStatus status);

    Page<RentRecord> findByTenantId(Long tenantId, Pageable pageable);

    List<RentRecord> findByRentMonthAndRentYear(Integer month, Integer year);

    @Query("SELECT COALESCE(SUM(r.totalPaid), 0) FROM RentRecord r            WHERE r.rentMonth = :month AND r.rentYear = :year")
    BigDecimal sumCollectedByMonthAndYear(Integer month, Integer year);

    @Query("SELECT r FROM RentRecord r WHERE r.status IN ('DUE', 'PARTIAL')")
    List<RentRecord> findAllOutstanding();
}
