package com.pg.tenant.service;

import com.pg.tenant.dto.AssignRoomRequest;
import com.pg.tenant.dto.CreateTenantRequest;
import com.pg.tenant.dto.TenantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TenantService {

    TenantResponse createTenant(CreateTenantRequest request);

    TenantResponse findById(Long id);

    Page<TenantResponse> findAll(String status, Pageable pageable);

    TenantResponse assignRoom(Long tenantId, AssignRoomRequest request);

    TenantResponse checkout(Long tenantId);

    // Called by Payment Service via REST to get active tenants for billing
    List<TenantResponse> findActiveTenants();
}
