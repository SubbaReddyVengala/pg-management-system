package com.pg.payment.controller;

import com.pg.payment.dto.RecordPaymentRequest;
import com.pg.payment.dto.RentRecordResponse;
import com.pg.payment.dto.PaymentResponse;
import com.pg.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management")
public class PaymentController {

    private final PaymentService paymentService;

    // Record a payment
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record a rent payment")
    public ResponseEntity<PaymentResponse> recordPayment(
            @Valid @RequestBody RecordPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.recordPayment(request));
    }

    // Generate monthly rent records for all active tenants
    @PostMapping("/generate-monthly")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate rent records for all active tenants for a month")
    public ResponseEntity<List<RentRecordResponse>> generateMonthly(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.generateMonthlyRecords(month, year));
    }

    // Get all outstanding dues
    @GetMapping("/dues")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all unpaid or partially paid rent records")
    public ResponseEntity<List<RentRecordResponse>> getDues() {
        return ResponseEntity.ok(paymentService.getOutstandingDues());
    }

    // Get rent records by tenant
    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get all rent records for a specific tenant")
    public ResponseEntity<Page<RentRecordResponse>> getByTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(paymentService.getByTenant(
                tenantId, PageRequest.of(page, size,
                        Sort.by(Sort.Direction.DESC, "rentYear", "rentMonth"))));
    }

    // Get individual payments for a rent record
    @GetMapping("/record/{rentRecordId}")
    @Operation(summary = "Get all payment transactions for a rent record")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByRecord(
            @PathVariable Long rentRecordId) {
        return ResponseEntity.ok(paymentService.getPaymentsByRecord(rentRecordId));
    }

    // Income summary — used by Report Service
    @GetMapping("/summary/{year}/{month}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get total rent collected for a given month and year")
    public ResponseEntity<BigDecimal> getSummary(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(paymentService.getTotalCollected(month, year));
    }
}
