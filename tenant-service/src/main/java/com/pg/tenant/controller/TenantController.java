package com.pg.tenant.controller;

import com.pg.tenant.dto.AssignRoomRequest;
import com.pg.tenant.dto.CreateTenantRequest;
import com.pg.tenant.dto.TenantResponse;
import com.pg.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant Management")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new tenant")
    public ResponseEntity<TenantResponse> create(
            @Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tenantService.createTenant(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tenants — paginated, filterable by status")
    public ResponseEntity<Page<TenantResponse>> getAll(
            @RequestParam(defaultValue = "0")           int page,
            @RequestParam(defaultValue = "10")          int size,
            @RequestParam(defaultValue = "fullName")    String sortBy,
            @RequestParam(defaultValue = "ASC")         String direction,
            @RequestParam(required = false)             String status) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sortBy));
        return ResponseEntity.ok(tenantService.findAll(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a tenant by ID")
    public ResponseEntity<TenantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @PostMapping("/{id}/assign-room")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign or change a room for a tenant")
    public ResponseEntity<TenantResponse> assignRoom(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoomRequest request) {
        return ResponseEntity.ok(tenantService.assignRoom(id, request));
    }

    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check out a tenant and free up their room")
    public ResponseEntity<TenantResponse> checkout(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.checkout(id));
    }

    // Internal endpoint called by Payment Service to get active tenants for billing
    @GetMapping("/active")
    @Operation(summary = "Get all active tenants — used internally by Payment Service")
    public ResponseEntity<List<TenantResponse>> getActive() {
        return ResponseEntity.ok(tenantService.findActiveTenants());
    }
}
