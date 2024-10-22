package io.hhplus.ecommerce.application.dto.cart;

import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.cart.Cart;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CartDto {

    private Long cartId;
    private Long userId;
    private UserDto user;
    private ProductDto product;
    private List<ProductDto> products;
    private int quantity;

    public Cart toEntity() {
        return Cart.builder()
                .product(this.product.toEntity())
                .user(this.user.toEntity())
                .quantity(this.quantity)
                .build();
    }

    public static CartDto toDto(Cart cart) {
        return CartDto.builder()
                .cartId(cart.getCartId())
                .user(UserDto.toDto(cart.getUser()))
                .product(ProductDto.toDto(cart.getProduct()))
                .quantity(cart.getQuantity())
                .build();
    }

}
