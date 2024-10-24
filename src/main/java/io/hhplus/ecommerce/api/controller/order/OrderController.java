package io.hhplus.ecommerce.api.controller.order;

import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/order")
@RequiredArgsConstructor
public class OrderController {

    private final PaymentFacade paymentFacade;

    @PostMapping
    public ResponseEntity<OrderDto> orderPayment(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(paymentFacade.orderPayment(orderDto));
    }

}
