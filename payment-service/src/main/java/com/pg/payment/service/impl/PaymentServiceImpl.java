package com.pg.payment.service.impl;

import com.pg.payment.client.RoomServiceClient;
import com.pg.payment.client.TenantServiceClient;
import com.pg.payment.client.dto.RoomResponse;
import com.pg.payment.client.dto.TenantResponse;
import com.pg.payment.dto.RecordPaymentRequest;
import com.pg.payment.dto.RentRecordResponse;
import com.pg.payment.dto.PaymentResponse;
import com.pg.payment.entity.Payment;
import com.pg.payment.entity.RentRecord;
import com.pg.payment.exception.BusinessException;
import com.pg.payment.exception.ResourceNotFoundException;
import com.pg.payment.repository.PaymentRepository;
import com.pg.payment.repository.RentRecordRepository;
import com.pg.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final RentRecordRepository rentRecordRepo;
    private final PaymentRepository    paymentRepo;
    private final TenantServiceClient  tenantClient;
    private final RoomServiceClient    roomClient;

    @Override
    public PaymentResponse recordPayment(RecordPaymentRequest request) {
        // Step 1: Find the rent record
        RentRecord record = rentRecordRepo.findById(request.getRentRecordId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rent record not found: " + request.getRentRecordId()));

        // Step 2: Validate payment amount
        BigDecimal due = record.getDueAmount();
        if (due.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Rent is already fully paid for this record");
        }
        if (request.getAmountPaid().compareTo(due) > 0) {
            throw new BusinessException(
                    "Amount exceeds due amount. Due: " + due +
                            ", Attempted: " + request.getAmountPaid());
        }

        // Step 3: Create payment entry
        Payment payment = Payment.builder()
                .rentRecord(record)
                .amountPaid(request.getAmountPaid())
                .paymentMode(request.getPaymentMode())
                .referenceNumber(request.getReferenceNumber())
                .paymentDate(LocalDateTime.now())
                .build();

        // Step 4: Apply to rent record (updates totalPaid and status)
        record.applyPayment(request.getAmountPaid());

        // Step 5: Save both
        rentRecordRepo.save(record);
        Payment saved = paymentRepo.save(payment);

        log.info("Payment recorded: tenantId={}, amount={}, status={}",
                record.getTenantId(), request.getAmountPaid(), record.getStatus());

        return PaymentResponse.from(saved);
    }

    @Override
    public List<RentRecordResponse> generateMonthlyRecords(Integer month, Integer year) {
        // Step 1: Get all active tenants via Feign
        List<TenantResponse> activeTenants = tenantClient.getActiveTenants();
        List<RentRecordResponse> generated = new ArrayList<>();

        for (TenantResponse tenant : activeTenants) {
            if (tenant.getRoomId() == null) continue;

            // Skip if record already exists for this tenant/month/year
            boolean exists = rentRecordRepo
                    .findByTenantIdAndRentMonthAndRentYear(
                            tenant.getId(), month, year).isPresent();
            if (exists) {
                log.info("Skipping tenant {} - record already exists for {}/{}",
                        tenant.getId(), month, year);
                continue;
            }

            // Step 2: Get rent amount from Room Service via Feign
            RoomResponse room = roomClient.getRoomById(tenant.getRoomId());

            // Step 3: Create the rent record
            RentRecord record = RentRecord.builder()
                    .tenantId(tenant.getId())
                    .roomId(tenant.getRoomId())
                    .rentMonth(month)
                    .rentYear(year)
                    .rentAmount(room.getMonthlyRent())
                    .build();

            generated.add(RentRecordResponse.from(rentRecordRepo.save(record)));
            log.info("Rent record created for tenant {} for {}/{}",
                    tenant.getId(), month, year);
        }

        log.info("Generated {} rent records for {}/{}", generated.size(), month, year);
        return generated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentRecordResponse> getOutstandingDues() {
        return rentRecordRepo.findAllOutstanding()
                .stream()
                .map(RentRecordResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentRecordResponse> getByTenant(Long tenantId, Pageable pageable) {
        return rentRecordRepo.findByTenantId(tenantId, pageable)
                .map(RentRecordResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByRecord(Long rentRecordId) {
        return paymentRepo.findByRentRecordId(rentRecordId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCollected(Integer month, Integer year) {
        return rentRecordRepo.sumCollectedByMonthAndYear(month, year);
    }
}
