package com.pg.payment.repository;

import com.pg.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByRentRecordId(Long rentRecordId);
}
