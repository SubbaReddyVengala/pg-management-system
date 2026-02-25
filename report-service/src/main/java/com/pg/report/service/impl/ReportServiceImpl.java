package com.pg.report.service.impl;

import com.pg.report.client.PaymentServiceClient;
import com.pg.report.client.RoomServiceClient;
import com.pg.report.client.TenantServiceClient;
import com.pg.report.client.dto.RentRecordResponse;
import com.pg.report.client.dto.RoomResponse;
import com.pg.report.client.dto.TenantResponse;
import com.pg.report.dto.DashboardResponse;
import com.pg.report.dto.OccupancyReport;
import com.pg.report.dto.RevenueReport;
import com.pg.report.dto.TenantDuesReport;
import com.pg.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final RoomServiceClient    roomClient;
    private final TenantServiceClient  tenantClient;
    private final PaymentServiceClient paymentClient;

    @Override
    public DashboardResponse getDashboard(Integer month, Integer year) {
        // Use current month/year if not specified
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year  != null) ? year  : now.getYear();

        // Fetch from all three services in parallel via Feign
        List<RoomResponse>       availableRooms  = roomClient.getAvailableRooms();
        List<TenantResponse>     activeTenants   = tenantClient.getActiveTenants();
        List<RentRecordResponse> outstandingDues = paymentClient.getOutstandingDues();
        BigDecimal               collected       = paymentClient.getTotalCollected(y, m);

        // Compute outstanding total
        BigDecimal totalDue = outstandingDues.stream()
                .map(RentRecordResponse::getDueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Occupancy rate: active tenants / total capacity
        // We approximate capacity as activeTenants + availableSlots from available rooms
        int totalAvailableSlots = availableRooms.stream()
                .mapToInt(r -> r.getAvailableSlots() != null ? r.getAvailableSlots() : 0)
                .sum();
        int totalCapacity = activeTenants.size() + totalAvailableSlots;
        double occupancyRate = totalCapacity > 0
                ? (activeTenants.size() * 100.0) / totalCapacity : 0.0;

        return DashboardResponse.builder()
                .totalRooms(availableRooms.size())  // approximate
                .availableRooms(availableRooms.size())
                .totalActiveTenants(activeTenants.size())
                .currentMonthCollection(collected != null ? collected : BigDecimal.ZERO)
                .outstandingDuesCount(outstandingDues.size())
                .totalOutstandingAmount(totalDue)
                .occupancyRatePercent(Math.round(occupancyRate * 10.0) / 10.0)
                .build();
    }

    @Override
    public OccupancyReport getOccupancyReport() {
        List<RoomResponse> available = roomClient.getAvailableRooms();
        List<TenantResponse> active  = tenantClient.getActiveTenants();

        int totalOccupied  = active.size();
        int totalAvailable = available.stream()
                .mapToInt(r -> r.getAvailableSlots() != null ? r.getAvailableSlots() : 0)
                .sum();
        int totalCapacity  = totalOccupied + totalAvailable;

        BigDecimal rentPotential = available.stream()
                .map(r -> r.getMonthlyRent() != null ? r.getMonthlyRent() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double occupancyRate = totalCapacity > 0
                ? BigDecimal.valueOf(totalOccupied * 100.0 / totalCapacity)
                .setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        // Floor breakdown from available rooms
        Map<Integer, List<RoomResponse>> byFloor = available.stream()
                .collect(Collectors.groupingBy(r ->
                        r.getFloor() != null ? r.getFloor() : 0));

        List<OccupancyReport.FloorStat> floorStats = byFloor.entrySet().stream()
                .map(e -> OccupancyReport.FloorStat.builder()
                        .floor(e.getKey())
                        .totalRooms(e.getValue().size())
                        .availableRooms(e.getValue().size())
                        .occupiedRooms(0)
                        .build())
                .sorted((a, b) -> Integer.compare(a.getFloor(), b.getFloor()))
                .toList();

        return OccupancyReport.builder()
                .totalRooms(available.size())
                .totalCapacity(totalCapacity)
                .totalOccupied(totalOccupied)
                .totalAvailable(totalAvailable)
                .occupancyRatePercent(occupancyRate)
                .totalMonthlyRentPotential(rentPotential)
                .floorBreakdown(floorStats)
                .build();
    }

    @Override
    public RevenueReport getRevenueReport(Integer month, Integer year) {
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year  != null) ? year  : now.getYear();

        List<RentRecordResponse> outstanding = paymentClient.getOutstandingDues();
        BigDecimal collected = paymentClient.getTotalCollected(y, m);
        if (collected == null) collected = BigDecimal.ZERO;

        // Filter dues for this month/year
        List<RentRecordResponse> monthDues = outstanding.stream()
                .filter(r -> r.getRentMonth() == m && r.getRentYear() == y)
                .toList();

        BigDecimal totalOutstanding = monthDues.stream()
                .map(RentRecordResponse::getDueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long dueCount     = monthDues.stream().filter(r -> "DUE".equals(r.getStatus())).count();
        long partialCount = monthDues.stream().filter(r -> "PARTIAL".equals(r.getStatus())).count();

        BigDecimal totalDue = collected.add(totalOutstanding);
        double collectionRate = totalDue.compareTo(BigDecimal.ZERO) > 0
                ? collected.multiply(BigDecimal.valueOf(100))
                .divide(totalDue, 1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        return RevenueReport.builder()
                .month(m)
                .year(y)
                .totalRentDue(totalDue)
                .totalCollected(collected)
                .totalOutstanding(totalOutstanding)
                .dueCount((int) dueCount)
                .partialCount((int) partialCount)
                .paidCount(0)   // Would need more data from Payment Service
                .collectionRatePercent(collectionRate)
                .build();
    }

    @Override
    public TenantDuesReport getTenantDuesReport() {
        List<RentRecordResponse> outstanding = paymentClient.getOutstandingDues();

        BigDecimal totalDue = outstanding.stream()
                .map(RentRecordResponse::getDueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TenantDuesReport.DueEntry> entries = outstanding.stream()
                .map(r -> TenantDuesReport.DueEntry.builder()
                        .tenantId(r.getTenantId())
                        .rentRecordId(r.getId())
                        .rentMonth(r.getRentMonth())
                        .rentYear(r.getRentYear())
                        .dueAmount(r.getDueAmount())
                        .status(r.getStatus())
                        .build())
                .toList();

        return TenantDuesReport.builder()
                .totalDefaulters(outstanding.size())
                .totalDueAmount(totalDue)
                .dues(entries)
                .build();
    }
}
