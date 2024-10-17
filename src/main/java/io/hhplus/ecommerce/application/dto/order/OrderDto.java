package io.hhplus.ecommerce.application.dto.order;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private Long orderId;
    private Long userId;
    private List<OrderDetailDto> orderDetails;
    private LocalDateTime orderDate;

}
