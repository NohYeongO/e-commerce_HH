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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindCartServiceTest {

    @Mock
    private CartJpaRepository cartJpaRepository;

    @InjectMocks
    private FindCartService findCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("사용자 ID로 장바구니 항목을 조회할 때 CartJpaRepository가 호출되고 올바른 결과를 반환")
    void getCartItems_Success() {
        // Given
        UserDto userDto = UserDto.builder().userId(1L).name("user").build();
        ProductDto productDto = ProductDto.builder().productId(1L).name("product").build();

        Cart cart1 = Cart.builder().user(userDto.toEntity()).product(productDto.toEntity()).build();  // 모킹할 Cart 엔티티
        Cart cart2 = Cart.builder().user(userDto.toEntity()).product(productDto.toEntity()).build();

        List<Cart> mockCarts = Arrays.asList(cart1, cart2);

        // CartJpaRepository가 반환할 값을 설정
        when(cartJpaRepository.findByUser_UserId(userDto.getUserId())).thenReturn(mockCarts);

        // When
        List<CartDto> result = findCartService.getCartItems(userDto);

        // Then
        assertEquals(2, result.size());  // CartDto 리스트의 크기 확인
        verify(cartJpaRepository, times(1)).findByUser_UserId(userDto.getUserId());  // 메서드 호출 확인
    }
}