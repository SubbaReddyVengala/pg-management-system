package com.pg.payment.service;

import com.pg.payment.dto.RecordPaymentRequest;
import com.pg.payment.dto.RentRecordResponse;
import com.pg.payment.dto.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    // Record a payment against a rent record
    PaymentResponse recordPayment(RecordPaymentRequest request);

    // Generate monthly rent records for all active tenants
    List<RentRecordResponse> generateMonthlyRecords(Integer month, Integer year);

    // Get all outstanding dues (DUE + PARTIAL)
    List<RentRecordResponse> getOutstandingDues();

    // Get all rent records for a specific tenant
    Page<RentRecordResponse> getByTenant(Long tenantId, Pageable pageable);

    // Get all payments for a specific rent record
    List<PaymentResponse> getPaymentsByRecord(Long rentRecordId);

    // Income summary for a given month/year (used by Report Service)
    BigDecimal getTotalCollected(Integer month, Integer year);
}
