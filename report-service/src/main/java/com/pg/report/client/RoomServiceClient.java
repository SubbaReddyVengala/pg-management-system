package com.pg.report.client;

import com.pg.report.client.dto.RoomResponse;
import com.pg.report.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "room-service", configuration = FeignConfig.class)
public interface RoomServiceClient {

    @GetMapping("/api/rooms/available")
    List<RoomResponse> getAvailableRooms();

    @GetMapping("/api/rooms")
    Object getAllRooms(@RequestParam(defaultValue = "0")  int page,
                       @RequestParam(defaultValue = "100") int size);
}
