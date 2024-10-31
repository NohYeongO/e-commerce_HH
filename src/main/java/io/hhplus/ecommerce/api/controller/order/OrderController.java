package io.hhplus.ecommerce.api.controller.order;

import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.service.OrderLockService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderLockService orderLockService;

    @PostMapping
    public ResponseEntity<OrderDto> orderPayment(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderLockService.orderPayment(orderDto));
    }

}
