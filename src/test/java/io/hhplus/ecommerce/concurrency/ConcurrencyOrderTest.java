package io.hhplus.ecommerce.concurrency;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class ConcurrencyOrderTest extends IntegrationTestSupport {

    private static final Logger log = LoggerFactory.getLogger(ConcurrencyOrderTest.class);
    private Long productId;

    @BeforeEach
    void setup() {
        // 상품재고 100개
        Product product = productJpaRepository.save(Product.builder().name("ice").price(BigDecimal.valueOf(5000)).stock(100).build());
        productId = product.getProductId();

        // 100명의 회원
        for (int i = 1; i <= 100; i++) {
            userJpaRepository.save(User.builder().name("회원" + i).point(BigDecimal.valueOf(10000)).build());
        }
    }

    @Test
    void testConcurrentOrders() throws InterruptedException {
        // 주문에 필요한 요청 데이터 준비
        List<OrderDetailDto> orderDetails = new ArrayList<>();
        OrderDetailDto detail = OrderDetailDto.builder().productId(productId).quantity(1).build();
        orderDetails.add(detail);

        AtomicInteger successfulOrders = new AtomicInteger(0);
        // 100명의 회원이 주문
        int numberOfOrders = 100;

        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= numberOfOrders; i++) {
            Long userId = (long) i;

            OrderDto orderDto = OrderDto.builder().userId(userId).orderDetails(orderDetails).build();

            Thread thread = new Thread(() -> {
                try {
                    paymentFacade.orderPayment(orderDto);
                    // 성공한 주문 수 증가
                    successfulOrders.incrementAndGet();
                } catch (Exception e) {
                    log.error("ERROR: {} ", e.getMessage());
                }
            });

            threads.add(thread);
            thread.start();
        }

        // 모든 스레드가 종료될 때까지 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // 결과 검증
        log.info("총 성공한 주문 수: {}", successfulOrders.get());
        Product updatedProduct = productJpaRepository.findById(productId).orElseThrow();
        assertEquals(0, updatedProduct.getStock());
    }


}
