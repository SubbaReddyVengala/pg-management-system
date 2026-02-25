package com.pg.tenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients                // <-- required for Feign to work
public class TenantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TenantServiceApplication.class, args);
    }
}
