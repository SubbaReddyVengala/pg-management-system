package com.pg.tenant.service.impl;

import com.pg.tenant.client.RoomServiceClient;
import com.pg.tenant.client.dto.RoomResponse;
import com.pg.tenant.dto.AssignRoomRequest;
import com.pg.tenant.dto.CreateTenantRequest;
import com.pg.tenant.dto.TenantResponse;
import com.pg.tenant.entity.Tenant;
import com.pg.tenant.enums.TenantStatus;
import com.pg.tenant.exception.BusinessException;
import com.pg.tenant.exception.ResourceNotFoundException;
import com.pg.tenant.repository.TenantRepository;
import com.pg.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository   tenantRepo;
    private final RoomServiceClient  roomClient;

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {
        if (tenantRepo.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        if (tenantRepo.existsByUserId(request.getUserId())) {
            throw new BusinessException("User already has a tenant profile");
        }

        Tenant tenant = Tenant.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .emergencyContact(request.getEmergencyContact())
                .joinDate(request.getJoinDate() != null
                        ? request.getJoinDate() : LocalDate.now())
                .securityDeposit(request.getSecurityDeposit() != null
                        ? request.getSecurityDeposit()
                        : java.math.BigDecimal.ZERO)
                .build();

        // If roomId provided at creation, assign immediately
        if (request.getRoomId() != null) {
            tenant = tenantRepo.save(tenant);
            return assignRoomInternal(tenant, request.getRoomId());
        }

        return TenantResponse.from(tenantRepo.save(tenant));
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse findById(Long id) {
        return TenantResponse.from(getTenantOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantResponse> findAll(String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            TenantStatus s = TenantStatus.valueOf(status.toUpperCase());
            return tenantRepo.findByStatus(s, pageable).map(TenantResponse::from);
        }
        return tenantRepo.findAll(pageable).map(TenantResponse::from);
    }

    @Override
    public TenantResponse assignRoom(Long tenantId, AssignRoomRequest request) {
        Tenant tenant = getTenantOrThrow(tenantId);
        return assignRoomInternal(tenant, request.getRoomId());
    }

    @Override
    public TenantResponse checkout(Long tenantId) {
        Tenant tenant = getTenantOrThrow(tenantId);

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new BusinessException("Tenant is not currently active");
        }

        // Free up the room via Feign call to Room Service
        if (tenant.getRoomId() != null) {
            roomClient.updateOccupancy(tenant.getRoomId(), -1);
            log.info("Room {} occupancy decremented for tenant checkout", tenant.getRoomId());
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        tenant.setRoomId(null);

        return TenantResponse.from(tenantRepo.save(tenant));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> findActiveTenants() {
        return tenantRepo.findByStatus(TenantStatus.ACTIVE)
                .stream()
                .map(TenantResponse::from)
                .toList();
    }

    // ── Private helpers ───────────────────────────────────

    private TenantResponse assignRoomInternal(Tenant tenant, Long roomId) {
        // Step 1: Fetch room from Room Service via Feign
        RoomResponse room = roomClient.getRoomById(roomId);

        // Step 2: Validate room is available
        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new BusinessException(
                    "Room " + room.getRoomNumber() + " is not available. Status: " + room.getStatus());
        }

        // Step 3: Check capacity
        if (room.getAvailableSlots() <= 0) {
            throw new BusinessException(
                    "Room " + room.getRoomNumber() + " is at full capacity");
        }

        // Step 4: If tenant had a previous room, vacate it first
        if (tenant.getRoomId() != null && !tenant.getRoomId().equals(roomId)) {
            roomClient.updateOccupancy(tenant.getRoomId(), -1);
            log.info("Previous room {} vacated for tenant {}", tenant.getRoomId(), tenant.getId());
        }

        // Step 5: Update Room Service occupancy
        roomClient.updateOccupancy(roomId, +1);
        log.info("Room {} occupancy incremented for tenant {}", roomId, tenant.getId());

        // Step 6: Update tenant record
        tenant.setRoomId(roomId);
        tenant.setStatus(TenantStatus.ACTIVE);

        return TenantResponse.from(tenantRepo.save(tenant));
    }

    private Tenant getTenantOrThrow(Long id) {
        return tenantRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tenant not found with id: " + id));
    }
}
