package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindProductServiceTest {
    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private FindProductService findProductService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("단일 상품을 성공적으로 조회하고, 상품의 ID, 이름, 가격, 재고가 이상없이 조회되는지 테스트")
    void productSuccess() {
        // given
        Long productId = 1L;
        String name = "phone";
        BigDecimal price = BigDecimal.valueOf(1000);
        int stock = 10;

        Product product = new Product(productId, name, price , stock);

        when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductDto productDto = findProductService.getProduct(productId);

        // then
        assertEquals(productId, productDto.getProductId());
        assertEquals(name, productDto.getName());
        assertEquals(price, productDto.getPrice());
        assertEquals(stock, productDto.getStock());
    }

    @Test
    @DisplayName("상품을 찾을 수 없을 때 Exception 처리 테스트")
    void productNotFound() {
        // given
        Long productId = 1L;
        when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());
        // when & then
        assertThrows(ResourceNotFoundException.class, () -> findProductService.getProduct(productId));
    }


    @Test
    @DisplayName("최근 3일간 판매된 상위 5개 상품을 조회하고, 결과가 이상없이 반환되는지 / 조건으로 들어갈 3일이 정확히 전달 되는지 테스트")
    void getTopFiveProductsWithDateConditionSuccess() {
        // Given: 가짜 Product 객체 목록 설정
        Product product1 = Product.builder()
                .productId(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(1000))
                .stock(10)
                .build();

        Product product2 = Product.builder()
                .productId(2L)
                .name("Product 2")
                .price(BigDecimal.valueOf(2000))
                .stock(20)
                .build();

        Product product3 = Product.builder()
                .productId(3L)
                .name("Product 3")
                .price(BigDecimal.valueOf(3000))
                .stock(30)
                .build();

        Product product4 = Product.builder()
                .productId(4L)
                .name("Product 4")
                .price(BigDecimal.valueOf(4000))
                .stock(40)
                .build();

        Product product5 = Product.builder()
                .productId(5L)
                .name("Product 5")
                .price(BigDecimal.valueOf(5000))
                .stock(50)
                .build();


        List<Product> mockProductList = Arrays.asList(product1, product2, product3, product4, product5);

        // 최근 3일의 시작 날짜 설정
        LocalDateTime startDate = LocalDate.now().minusDays(3).atStartOfDay();

        // When: productJpaRepository.findTop5Product()가 호출되면 가짜 리스트를 반환하도록 설정
        when(productJpaRepository.findTop5Product(startDate)).thenReturn(mockProductList);

        // 서비스 메소드 호출
        List<ProductDto> topProducts = findProductService.getTopFiveProducts();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(productJpaRepository, times(1)).findTop5Product(captor.capture());

        // 캡쳐를 활용해 조건에 들어갈 3일을 잘 전달하는지 확인
        LocalDateTime capturedDate = captor.getValue();
        assertThat(capturedDate).isEqualTo(startDate);

        // 반환된 리스트의 크기와 각 상품의 정보를 검증
        assertThat(topProducts).hasSize(5);  // 5개의 상품이 반환되었는지 확인

        // 상품들의 속성을 하나씩 검증
        assertThat(topProducts.get(0).getProductId()).isEqualTo(1L);
        assertThat(topProducts.get(0).getName()).isEqualTo("Product 1");
        assertThat(topProducts.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(topProducts.get(0).getStock()).isEqualTo(10);

        // 나머지 상품들도 동일하게 검증
        assertThat(topProducts.get(1).getProductId()).isEqualTo(2L);
        assertThat(topProducts.get(1).getName()).isEqualTo("Product 2");
        assertThat(topProducts.get(1).getPrice()).isEqualTo(BigDecimal.valueOf(2000));
        assertThat(topProducts.get(1).getStock()).isEqualTo(20);

        assertThat(topProducts.get(2).getProductId()).isEqualTo(3L);
        assertThat(topProducts.get(2).getName()).isEqualTo("Product 3");
        assertThat(topProducts.get(2).getPrice()).isEqualTo(BigDecimal.valueOf(3000));
        assertThat(topProducts.get(2).getStock()).isEqualTo(30);

        assertThat(topProducts.get(3).getProductId()).isEqualTo(4L);
        assertThat(topProducts.get(3).getName()).isEqualTo("Product 4");
        assertThat(topProducts.get(3).getPrice()).isEqualTo(BigDecimal.valueOf(4000));
        assertThat(topProducts.get(3).getStock()).isEqualTo(40);

        assertThat(topProducts.get(4).getProductId()).isEqualTo(5L);
        assertThat(topProducts.get(4).getName()).isEqualTo("Product 5");
        assertThat(topProducts.get(4).getPrice()).isEqualTo(BigDecimal.valueOf(5000));
        assertThat(topProducts.get(4).getStock()).isEqualTo(50);
    }

    @Test
    @DisplayName("판매 상위 5개 제품 조회시 조회되는 데이터가 없을경우 예외처리 테스트")
    void find_Top_Five_EmptyTest() {
        // given
        List<Product> topProducts = List.of();
        // when
        when(productJpaRepository.findTop5Product(LocalDate.now().minusDays(3).atStartOfDay())).thenReturn(topProducts);
        // then
        assertThrows(ResourceNotFoundException.class, () -> findProductService.getTopFiveProducts());
    }


}