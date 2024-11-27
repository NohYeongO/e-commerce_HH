package io.hhplus.ecommerce.application.event;

import io.hhplus.ecommerce.application.dto.order.OrderDto;
import lombok.Getter;

@Getter
public class OrderFailedEvent {

    private final OrderDto orderDto;
    private final String reason;

    public OrderFailedEvent(OrderDto orderDto, String reason) {
        this.orderDto = orderDto;
        this.reason = reason;
    }
}
