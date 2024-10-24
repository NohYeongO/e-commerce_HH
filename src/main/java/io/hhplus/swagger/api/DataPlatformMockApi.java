package io.hhplus.swagger.api;

import io.hhplus.ecommerce.application.dto.order.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataPlatformMockApi {

    private static final Logger log = LoggerFactory.getLogger(DataPlatformMockApi.class);

    @PostMapping("/orders")
    public ResponseEntity<Boolean> receiveOrderData(@RequestBody OrderDto orderDto) {
        log.info("Order received: {}", orderDto);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
