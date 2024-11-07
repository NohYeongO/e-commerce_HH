package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.order.Order;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FindProductCacheTest extends IntegrationTestSupport {

    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setUpTestData();
    }

    private void setUpTestData() {
        // 사용자 생성
        User user = User.builder()
                .name("Test User")
                .point(new BigDecimal("1000.00"))
                .build();
        userJpaRepository.save(user);

        // 제품 생성
        Product product1 = Product.builder()
                .name("Product 1")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .build();
        Product product2 = Product.builder()
                .name("Product 2")
                .price(new BigDecimal("200.00"))
                .stock(30)
                .build();
        productJpaRepository.saveAll(Arrays.asList(product1, product2));

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderDetails(Arrays.asList(
                        OrderDetail.builder().product(product1).quantity(2).build(),
                        OrderDetail.builder().product(product2).quantity(1).build()
                ))
                .build();
        orderJpaRepository.save(order);
    }

    @Test
    @DisplayName("Redis Cache에 상위 5개 제품이 잘 저장되고 조회되는지 확인")
    public void getTopFiveProducts_CacheTest() {
        // Given
        List<Product> products = Arrays.asList(
                Product.builder().productId(1L).name("Product 1").price(new BigDecimal("100.00")).stock(50).build(),
                Product.builder().productId(2L).name("Product 2").price(new BigDecimal("200.00")).stock(30).build()
        );

        // Fetch top products and cache them
        List<ProductDto> topProducts = findProductService.getTopFiveProducts();
        assertEquals(2, topProducts.size());
        assertEquals("Product 1", topProducts.get(0).getName());
        assertEquals("Product 2", topProducts.get(1).getName());

        // Verify that products are cached in Redis
        RBucket<List<ProductDto>> cachedProductsBucket = redissonClient.getBucket("topProductsCache::topfiveproduct");
        List<ProductDto> cachedProducts = cachedProductsBucket.get();
        assertThat(cachedProducts).isNotNull();
        assertEquals(2, cachedProducts.size());
        assertEquals("Product 1", cachedProducts.get(0).getName());
        assertEquals("Product 2", cachedProducts.get(1).getName());

        // Call again to check if cache works
        List<ProductDto> cachedProductsAgain = findProductService.getTopFiveProducts();
        assertEquals(2, cachedProductsAgain.size());
    }
}
