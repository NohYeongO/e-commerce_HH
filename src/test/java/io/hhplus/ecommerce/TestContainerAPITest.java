package io.hhplus.ecommerce;

import io.hhplus.ecommerce.domain.entity.order.Order;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class TestContainerAPITest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testDB")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureTestContainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductJpaRepository productJpaRepository;
    
    @BeforeEach
    void setUp() {
        Product product = Product.builder().productId(1L).name("phone").price(BigDecimal.valueOf(10000)).stock(50).build();
        productJpaRepository.save(product);
    }


    @Test
    void getProductApiTest() {
        ResponseEntity<String> response = restTemplate.getForEntity("/product/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
