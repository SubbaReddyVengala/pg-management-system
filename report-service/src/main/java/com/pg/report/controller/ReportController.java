package com.pg.report.controller;

import com.pg.report.dto.DashboardResponse;
import com.pg.report.dto.OccupancyReport;
import com.pg.report.dto.RevenueReport;
import com.pg.report.dto.TenantDuesReport;
import com.pg.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports & Analytics")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin dashboard — all key metrics in one call")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getDashboard(month, year));
    }

    @GetMapping("/occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Room occupancy breakdown by floor")
    public ResponseEntity<OccupancyReport> getOccupancy() {
        return ResponseEntity.ok(reportService.getOccupancyReport());
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Monthly revenue collection report")
    public ResponseEntity<RevenueReport> getRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(reportService.getRevenueReport(month, year));
    }

    @GetMapping("/dues")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "All tenants with outstanding rent dues")
    public ResponseEntity<TenantDuesReport> getDues() {
        return ResponseEntity.ok(reportService.getTenantDuesReport());
    }
}
