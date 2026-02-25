package com.pg.tenant.repository;

import com.pg.tenant.entity.Tenant;
import com.pg.tenant.enums.TenantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUserId(Long userId);

    Page<Tenant> findByStatus(TenantStatus status, Pageable pageable);

    // Used by Payment Service via Feign to get active tenants
    List<Tenant> findByStatus(TenantStatus status);

    List<Tenant> findByRoomId(Long roomId);

    Optional<Tenant> findByUserIdAndStatus(Long userId, TenantStatus status);
}
