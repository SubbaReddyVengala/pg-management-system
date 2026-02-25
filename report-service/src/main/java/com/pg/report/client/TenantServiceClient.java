package com.pg.report.client;

import com.pg.report.client.dto.TenantResponse;
import com.pg.report.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "tenant-service", configuration = FeignConfig.class)
public interface TenantServiceClient {

    @GetMapping("/api/tenants/active")
    List<TenantResponse> getActiveTenants();

    @GetMapping("/api/tenants")
    Object getAllTenants(@RequestParam(defaultValue = "0")   int page,
                         @RequestParam(defaultValue = "100") int size,
                         @RequestParam(required = false)     String status);
}
