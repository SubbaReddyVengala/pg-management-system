package com.pg.report.service;

import com.pg.report.dto.DashboardResponse;
import com.pg.report.dto.OccupancyReport;
import com.pg.report.dto.RevenueReport;
import com.pg.report.dto.TenantDuesReport;

public interface ReportService {

    // Main admin dashboard — aggregates all key metrics
    DashboardResponse getDashboard(Integer month, Integer year);

    // Detailed room occupancy breakdown
    OccupancyReport getOccupancyReport();

    // Revenue collection report for a specific month
    RevenueReport getRevenueReport(Integer month, Integer year);

    // List all tenants with outstanding dues
    TenantDuesReport getTenantDuesReport();
}
