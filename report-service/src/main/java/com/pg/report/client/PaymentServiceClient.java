package com.pg.report.client;

import com.pg.report.client.dto.RentRecordResponse;
import com.pg.report.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentServiceClient {

    @GetMapping("/api/payments/dues")
    List<RentRecordResponse> getOutstandingDues();

    @GetMapping("/api/payments/summary/{year}/{month}")
    BigDecimal getTotalCollected(@PathVariable("year") Integer year,
                                 @PathVariable("month") Integer month);
}
