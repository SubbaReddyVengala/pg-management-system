package com.pg.tenant.client;

import com.pg.tenant.client.dto.RoomResponse;
import com.pg.tenant.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "room-service", configuration = FeignConfig.class)
public interface RoomServiceClient {

    @GetMapping("/api/rooms/{id}")
    RoomResponse getRoomById(@PathVariable("id") Long id);

    @PutMapping("/api/rooms/{id}/occupancy")
    void updateOccupancy(@PathVariable("id") Long id,
                         @RequestParam("delta") int delta);
}