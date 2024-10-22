package io.hhplus.ecommerce.domain.service.cart;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.cart.Cart;
import io.hhplus.ecommerce.infra.cart.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateCartService {

    private final CartJpaRepository cartJpaRepository;

    // 장바구니에 상품 추가
    public void addCart(CartDto cartDto) {
        for (ProductDto productDto : cartDto.getProducts()) {
            Cart cart = Cart.builder()
                    .user(cartDto.getUser().toEntity())
                    .product(productDto.toEntity())
                    .quantity(productDto.getQuantity())
                    .build();
            cartJpaRepository.save(cart);
        }
    }

    // 장바구니에서 상품 삭제
    public void removeCart(Long cartId) {
        cartJpaRepository.deleteById(cartId);
    }
}
