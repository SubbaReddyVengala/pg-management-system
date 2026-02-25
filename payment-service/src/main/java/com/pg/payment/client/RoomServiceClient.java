package com.pg.payment.client;

import com.pg.payment.client.dto.RoomResponse;
import com.pg.payment.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service", configuration = FeignConfig.class)
public interface RoomServiceClient {

    // Gets room details to fetch the current monthly rent amount
    @GetMapping("/api/rooms/{id}")
    RoomResponse getRoomById(@PathVariable("id") Long id);
}
