package io.hhplus.ecommerce.domain.service.cart;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.cart.Cart;
import io.hhplus.ecommerce.infra.cart.CartJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UpdateFindCartServiceTest {

    @Mock
    private CartJpaRepository cartJpaRepository;

    @InjectMocks
    private UpdateCartService updateCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("장바구니에 상품을 성공적으로 추가하면 CartJpaRepository.save()가 상품 개수만큼 호출")
    void addCart_Success() {
        // Given
        ProductDto product1 = ProductDto.builder().productId(1L).quantity(2).build();
        ProductDto product2 = ProductDto.builder().productId(2L).quantity(1).build();

        UserDto user = UserDto.builder()
                        .userId(1L)
                        .name("user")
                        .build();

        CartDto cartDto = CartDto.builder()
                .user(user)
                .products(Arrays.asList(product1, product2))
                .build();

        // When
        updateCartService.addCart(cartDto);

        // Then
        verify(cartJpaRepository, times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니에서 상품을 삭제하면 CartJpaRepository.deleteById()가 호출")
    void removeCart_Success() {
        // Given
        Long cartId = 1L;

        // When
        updateCartService.removeCart(cartId);

        // Then
        verify(cartJpaRepository, times(1)).deleteById(cartId);
    }

}