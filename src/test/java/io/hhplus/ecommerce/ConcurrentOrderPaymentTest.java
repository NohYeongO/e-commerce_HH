package io.hhplus.ecommerce;


import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class ConcurrentOrderPaymentTest {

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
    private PaymentFacade paymentFacade;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        // 회원 Mock 객체 생성
        user = userJpaRepository.save(User.builder().name("user").point(BigDecimal.valueOf(200000)).build());
        // 상품 Mock 객체 생성
        product = productJpaRepository.save(Product.builder().name("Phone").price(BigDecimal.valueOf(10000)).stock(100).build());
    }

    @Test
    @DisplayName("동시 주문에 대한 테스트")
    void concurrentOrderPaymentTest() throws ExecutionException, InterruptedException {
        int threadCount = 10; // 10명의 사용자가 동시에 주문을 시도한다고 가정
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 동시에 주문을 처리
        List<Callable<OrderDto>> orderTasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            orderTasks.add(() -> {

                // OrderDto 생성 로직
                List<OrderDetailDto> orderDetails = List.of(
                        OrderDetailDto.builder().productId(product.getProductId()).quantity(2).build()
                );

                OrderDto orderDto = OrderDto.builder()
                        .userId(user.getUserId())
                        .orderDetails(orderDetails)
                        .build();

                return paymentFacade.orderPayment(orderDto); // 주문 처리
            });
        }

        // 모든 주문이 성공했는지 확인할때 사용
        List<Future<OrderDto>> futures = executorService.invokeAll(orderTasks);

        for (Future<OrderDto> future : futures) {
            OrderDto orderDto = future.get();
            assertThat(orderDto).isNotNull();
            assertThat(orderDto.getOrderDetails()).isNotEmpty();
        }

        // 재고 차감이 올바르게 수행되었는지 확인
        Product updatedProduct = productJpaRepository.findById(product.getProductId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(100 - (threadCount * 2));

        // 종료
        executorService.shutdown();
    }


}
