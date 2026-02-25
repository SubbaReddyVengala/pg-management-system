package com.pg.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTenantRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String emergencyContact;

    private LocalDate joinDate;

    @DecimalMin(value = "0.0", message = "Deposit cannot be negative")
    private BigDecimal securityDeposit;

    // Optional: assign room at creation time
    private Long roomId;
}
