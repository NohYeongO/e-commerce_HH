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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("장바구니에 null 상품이 섞여 있어도 나머지 상품은 추가된다")
    void addCartWithNullProducts() {
        // given: null 상품2개 유요한 상품 2개
        ProductDto product1 = ProductDto.builder()
                .productId(1L)
                .name("Valid Product 1")
                .quantity(2)
                .build();

        ProductDto product2 = ProductDto.builder()
                .productId(2L)
                .name("Valid Product 2")
                .quantity(3)
                .build();

        ProductDto nullProduct1 = null;
        ProductDto nullProduct2 = null;

        CartDto cartDto = CartDto.builder()
                .user(new UserDto())
                .products(Arrays.asList(product1, nullProduct1, product2, nullProduct2))
                .build();

        updateCartService.addCart(cartDto);

        // Then: 유효한 2개의 상품만 저장되었는지 확인
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartJpaRepository, times(2)).save(cartCaptor.capture());

        // 캡처된 저장된 상품들 확인
        assertThat(cartCaptor.getAllValues().get(0).getProduct().getProductId()).isEqualTo(1L);
        assertThat(cartCaptor.getAllValues().get(0).getQuantity()).isEqualTo(2);

        assertThat(cartCaptor.getAllValues().get(1).getProduct().getProductId()).isEqualTo(2L);
        assertThat(cartCaptor.getAllValues().get(1).getQuantity()).isEqualTo(3);

        // 밑에 코드는 상호작용 됐는지 검사 할 수 있다고 하는데 아직은 어떻게 사용하는지 모르겠음
        // verifyNoMoreInteractions(cartJpaRepository);
    }

    @Test
    @DisplayName("cartId가 null일 경우 예외 발생시 예외처리 테스트")
    void removeNullCartId() {
        // Given: null인 cartId
        Long cartId = null;

        assertThrows(CartOperationException.class, () -> updateCartService.removeCart(cartId));

        // cartJpaRepository.deleteById는 호출되지 않아야 함
        verify(cartJpaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("유효한 cartId로 장바구니에 상품 삭제요청되는지 테스트")
    void removeCartId() {
        // Given: 유효한 cartId
        Long cartId = 1L;
        // When: removeCart 호출
        updateCartService.removeCart(cartId);
        // Then: deleteById가 정상적으로 호출되었는지 확인
        verify(cartJpaRepository, times(1)).deleteById(cartId);
    }

}