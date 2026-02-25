package com.pg.payment.client.dto;

import lombok.Data;

@Data
public class TenantResponse {
    private Long   id;
    private Long   roomId;
    private String fullName;
    private String email;
    private String status;
}
