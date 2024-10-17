package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.service.product.ProductService;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 리스트 조회")
    void productsSuccess() {
        // given
        Product product1 = new Product(1L, "phone", BigDecimal.valueOf(1000), 10);
        Product product2 = new Product(2L, "Laptop", BigDecimal.valueOf(2000), 20);
        List<Product> productList = List.of(product1, product2);
        List<Long> productIdList = Arrays.asList(1L, 2L);
        when(productJpaRepository.findAllById(productIdList)).thenReturn(productList);

        // when
        List<ProductDto> products = productService.getProducts(productIdList);

        // then
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getProductId()).isEqualTo(1L);
        assertThat(products.get(1).getProductId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("상품 단일 조회")
    void productSuccess() {
        // given
        Long productId = 1L;
        String name = "phone";
        BigDecimal price = BigDecimal.valueOf(1000);
        int stock = 10;

        Product product = new Product(productId, name, price , stock);

        when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductDto productDto = productService.getProduct(productId);

        // then
        assertEquals(productId, productDto.getProductId());
        assertEquals(name, productDto.getName());
        assertEquals(price, productDto.getPrice());
        assertEquals(stock, productDto.getStock());
    }

    @Test
    @DisplayName("상품 단일 조회 실패 - 상품을 찾을 수 없음")
    void productNotFound() {
        // given
        Long productId = 1L;

        when(productJpaRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId));
    }


    @Test
    @DisplayName("재고 수량이 없을 경우")
    void stockZero() {
        // given
        Long productId = 1L;
        Product product = new Product(1L, "phone", BigDecimal.valueOf(1000), 0);
        when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));
        // when & then
        assertThat(product.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("판매 상위 5개 조회 확인")
    void getTopFiveTest() {
        // Given: 가짜 Product 객체 목록을 미리 설정
        Product product1 = new Product(1L, "Product 1", BigDecimal.valueOf(1000), 10);
        Product product2 = new Product(2L, "Product 2", BigDecimal.valueOf(2000), 20);
        Product product3 = new Product(3L, "Product 3", BigDecimal.valueOf(3000), 30);
        Product product4 = new Product(4L, "Product 4", BigDecimal.valueOf(4000), 40);
        Product product5 = new Product(5L, "Product 5", BigDecimal.valueOf(5000), 50);

        List<Product> mockProductList = Arrays.asList(product1, product2, product3, product4, product5);

        // When: productJpaRepository.findTop5Product()가 호출되면 가짜 리스트를 반환하도록 설정
        when(productJpaRepository.findTop5Product()).thenReturn(mockProductList);

        // Then: 서비스 메소드가 제대로 작동하는지 검증
        List<ProductDto> topProducts = productService.getTopFiveProducts();

        // 검증
        assertEquals(5, topProducts.size()); // 결과가 5개의 제품인지 확인
    }
}