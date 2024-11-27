package io.hhplus.ecommerce.application.event;

import io.hhplus.ecommerce.application.dto.order.OrderDto;
import lombok.Getter;

@Getter
public class OrderSuccessEvent {

    private final OrderDto order;

    public OrderSuccessEvent(OrderDto order) {
        this.order = order;
    }
}
