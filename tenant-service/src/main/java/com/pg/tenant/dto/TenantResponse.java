package com.pg.tenant.dto;

import com.pg.tenant.entity.Tenant;
import com.pg.tenant.enums.TenantStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TenantResponse {

    private Long          id;
    private Long          userId;
    private Long          roomId;
    private String        fullName;
    private String        email;
    private String        phone;
    private String        emergencyContact;
    private LocalDate     joinDate;
    private BigDecimal    securityDeposit;
    private TenantStatus  status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TenantResponse from(Tenant t) {
        return TenantResponse.builder()
                .id(t.getId())
                .userId(t.getUserId())
                .roomId(t.getRoomId())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .phone(t.getPhone())
                .emergencyContact(t.getEmergencyContact())
                .joinDate(t.getJoinDate())
                .securityDeposit(t.getSecurityDeposit())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
