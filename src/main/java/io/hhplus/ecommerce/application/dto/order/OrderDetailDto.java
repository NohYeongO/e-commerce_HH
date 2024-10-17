package io.hhplus.ecommerce.application.dto.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDto {

    private Long detailId;
    private Long orderId;
    private Long productId;
    private int quantity;

}
