package io.hhplus.ecommerce.domain.service.cart;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.CartOperationException;
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
    @DisplayName("사용자 ID로 장바구니 항목을 조회할 때 CartJpaRepository가 호출되고 결과를 이상없이 반환하는지 테스트")
    void getCartItems_Success() {
        // Given
        UserDto userDto = UserDto.builder().userId(1L).name("user").build();
        ProductDto productDto = ProductDto.builder().productId(1L).name("product").build();

        Cart cart1 = Cart.builder().user(userDto.toEntity()).product(productDto.toEntity()).build();
        Cart cart2 = Cart.builder().user(userDto.toEntity()).product(productDto.toEntity()).build();

        List<Cart> mockCarts = Arrays.asList(cart1, cart2);

        // CartJpaRepository가 반환할 값을 설정
        when(cartJpaRepository.findByUser_UserId(userDto.getUserId())).thenReturn(mockCarts);

        // When
        List<CartDto> result = findCartService.getCartItems(userDto);

        // Then
        assertEquals(2, result.size());
        verify(cartJpaRepository, times(1)).findByUser_UserId(userDto.getUserId());
    }

    @Test
    @DisplayName("장바구니 조회시 조회되는 데이터가 없을경우 예외처리 테스트")
    void cartEmpty_Test() {
        // given
        UserDto userDto = UserDto.builder().userId(1L).name("user").build();
        List<Cart> carts = List.of();
        // when
        when(cartJpaRepository.findByUser_UserId(userDto.getUserId())).thenReturn(carts);

        // then
        assertThrows(CartOperationException.class, () -> findCartService.getCartItems(userDto));

    }





}