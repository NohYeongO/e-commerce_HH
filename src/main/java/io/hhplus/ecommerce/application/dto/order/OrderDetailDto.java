package io.hhplus.ecommerce.application.dto.order;

import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.product.Product;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDto {

    private Long detailId;
    private Long orderId;
    private Long productId;
    private int quantity;

    private ProductDto productDto;

    public static OrderDetailDto toDto(OrderDetail orderDetail) {
        return OrderDetailDto.builder()
                .productDto(ProductDto.toDto(orderDetail.getProduct()))
                .quantity(orderDetail.getQuantity())
                .build();
    }

    public OrderDetail toEntity(){
        return OrderDetail.builder()
                .product(this.productDto.toEntity())
                .quantity(this.quantity).build();
    }


}
