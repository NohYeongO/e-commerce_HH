package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StockDeductionServiceTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private StockDeductionService stockDeductionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("성공적으로 재고 차감 후, OrderDetailDto 리스트를 반환")
    void stockDeductionSuccess() {
        // given
        Product product1 = new Product(1L, "Phone", BigDecimal.valueOf(1000), 10);
        Product product2 = new Product(2L, "Laptop", BigDecimal.valueOf(2000), 20);

        List<Long> productIds = Arrays.asList(1L, 2L);
        List<Product> products = Arrays.asList(product1, product2);

        when(productJpaRepository.findAllById(productIds)).thenReturn(products);

        OrderDetailDto orderDetail1 = OrderDetailDto.builder()
                .productId(1L)
                .quantity(2)
                .productDto(ProductDto.builder()
                        .productId(1L)
                        .name("Phone")
                        .price(BigDecimal.valueOf(1000))
                        .build())
                .build();

        OrderDetailDto orderDetail2 = OrderDetailDto.builder()
                .productId(2L)
                .quantity(3)
                .productDto(ProductDto.builder()
                        .productId(2L)
                        .name("Laptop")
                        .price(BigDecimal.valueOf(2000))
                        .build())
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderDetails(Arrays.asList(orderDetail1, orderDetail2))
                .build();

        // when
        List<OrderDetailDto> result = stockDeductionService.stockDeduction(orderDto);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductDto().getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getQuantity()).isEqualTo(2);
        assertThat(result.get(1).getProductDto().getProductId()).isEqualTo(2L);
        assertThat(result.get(1).getQuantity()).isEqualTo(3);
        assertThat(product1.getStock()).isEqualTo(8); // 10 - 2
        assertThat(product2.getStock()).isEqualTo(17); // 20 - 3
    }

    @Test
    @DisplayName("상품이 존재하지 않을 경우 ProductNotFoundException을 발생")
    void productNotFound() {
        // given
        List<Long> productIds = List.of(1L);
        when(productJpaRepository.findAllById(productIds)).thenReturn(List.of());

        OrderDetailDto orderDetail = OrderDetailDto.builder()
                .productId(1L)
                .quantity(2)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderDetails(List.of(orderDetail))
                .build();

        // when & then
        assertThrows(ProductNotFoundException.class, () -> stockDeductionService.stockDeduction(orderDto));
    }

    @Test
    @DisplayName("재고 차감 시 상품이 없을 경우 ProductNotFoundException 발생")
    void productMapNotFound() {
        // given
        Product product = new Product(1L, "Phone", BigDecimal.valueOf(1000), 10);
        List<Long> productIds = List.of(1L);
        when(productJpaRepository.findAllById(productIds)).thenReturn(List.of(product));

        OrderDetailDto orderDetail = OrderDetailDto.builder()
                .productId(2L) // 존재하지 않는 상품 ID
                .quantity(2)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .orderDetails(List.of(orderDetail))
                .build();

        // when & then
        assertThrows(ProductNotFoundException.class, () -> stockDeductionService.stockDeduction(orderDto));
    }

}