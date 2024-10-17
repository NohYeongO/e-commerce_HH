package io.hhplus.ecommerce.domain.service.order;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.order.Order;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    private UserDto userDto;
    private List<ProductDto> productsDto;
    private List<OrderDetailDto> orderDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 사용자 초기화
        userDto = UserDto.builder()
                .userId(1L)
                .name("영오")
                .point(BigDecimal.valueOf(50000))  // 사용자의 초기 포인트 설정
                .build();

        // 상품 초기화
        ProductDto productDto = ProductDto.builder()
                .productId(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(10000))  // 상품 가격 설정
                .stock(100)  // 상품 재고 설정
                .build();

        productsDto = Arrays.asList(productDto);

        // 주문 상세 내역 초기화
        OrderDetailDto orderDetailDto = OrderDetailDto.builder()
                .productId(1L)
                .quantity(2)  // 주문 수량 설정
                .build();

        orderDetails = Arrays.asList(orderDetailDto);
    }

    @Test
    @DisplayName("주문이 잘 되는지 테스트")
    void testOrderPayment() {
        // Given
        User user = userDto.toEntity();
        Product product = productsDto.get(0).toEntity();
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderDetails(new ArrayList<>())
                .build();

        // Mock 동작 설정
        when(userJpaRepository.save(any(User.class))).thenReturn(user);
        when(productJpaRepository.saveAndFlush(any(Product.class))).thenReturn(product);
        when(orderJpaRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderDto result = orderService.orderPayment(userDto, productsDto, orderDetails);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());  // User ID 확인
        assertEquals(1, result.getOrderDetails().size());  // 주문 상세 내역 크기 확인
        assertEquals(1L, result.getOrderDetails().get(0).getProductId());  // 첫 번째 주문 상품 ID 확인
        assertEquals(2, result.getOrderDetails().get(0).getQuantity());  // 주문한 수량 확인
    }

}