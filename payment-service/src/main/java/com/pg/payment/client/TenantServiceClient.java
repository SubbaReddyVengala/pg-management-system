package com.pg.payment.client;

import com.pg.payment.client.dto.TenantResponse;
import com.pg.payment.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "tenant-service", configuration = FeignConfig.class)
public interface TenantServiceClient {

    // Gets all active tenants for monthly rent generation
    @GetMapping("/api/tenants/active")
    List<TenantResponse> getActiveTenants();
}
