package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentFacadeTest extends IntegrationTestSupport {

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder().name("note").price(BigDecimal.valueOf(1000)).stock(100).build();
        product2 = Product.builder().name("iphone").price(BigDecimal.valueOf(1000)).stock(100).build();
        productJpaRepository.saveAll(List.of(product1, product2));
    }

    @Test
    @DisplayName("한 명의 회원이 충전과 주문을 동시에 요청할 때 동시성 테스트")
    void testOrderPaymentAndChargeConcurrency() throws InterruptedException {

        BigDecimal firstPoint = BigDecimal.valueOf(10000);
        User user = userJpaRepository.save(User.builder().name("test1").point(firstPoint).build());

        int concurrentRequests = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);

        // 주문과 충전에 필요한 요청 데이터 준비
        OrderDetailDto detail1 = OrderDetailDto.builder().productId(product1.getProductId()).quantity(1).build();
        OrderDetailDto detail2 = OrderDetailDto.builder().productId(product2.getProductId()).quantity(1).build();
        List<OrderDetailDto> orderDetails = List.of(detail1, detail2);

        List<Callable<Void>> tasks = new ArrayList<>();

        // 주문 작업 스레드
        tasks.add(() -> {
            OrderDto orderDto = OrderDto.builder().userId(user.getUserId()).orderDetails(orderDetails).build();
            paymentFacade.orderPayment(orderDto);
            return null;
        });

        // 충전 작업 스레드
        tasks.add(() -> {
            ChargeRequest chargeRequest = new ChargeRequest(user.getUserId(), BigDecimal.valueOf(10000));
            paymentFacade.charge(chargeRequest);
            return null;
        });

        List<Future<Void>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();

        assertDoesNotThrow(() -> {
            for (Future<Void> future : futures) {
                future.get();
            }
        });

        // 검증
        User updatedUser = userJpaRepository.findById(user.getUserId()).orElseThrow();
        Product updatedProduct1 = productJpaRepository.findById(product1.getProductId()).orElseThrow();
        Product updatedProduct2 = productJpaRepository.findById(product2.getProductId()).orElseThrow();

        assertEquals(product1.getStock() - 1, updatedProduct1.getStock(), "Product 1 재고 검증");
        assertEquals(product2.getStock() - 1, updatedProduct2.getStock(), "Product 2 재고 검증");
        assertEquals(firstPoint.add(BigDecimal.valueOf(10000)).subtract(product1.getPrice().add(product2.getPrice())).setScale(0, RoundingMode.DOWN), updatedUser.getPoint().setScale(0, RoundingMode.DOWN), "회원 포인트 검증");
    }

    @Test
    @DisplayName("잔고부족으로 인한 예외 발생 시 차감된 재고 추가되는지 테스트")
    void exception_ProductAddTest() {
        User user = userJpaRepository.save(User.builder().name("test1").point(BigDecimal.ZERO).build());

        OrderDetailDto detail1 = OrderDetailDto.builder().productId(product1.getProductId()).quantity(1).build();
        OrderDetailDto detail2 = OrderDetailDto.builder().productId(product2.getProductId()).quantity(1).build();

        try {
            OrderDto orderDto = OrderDto.builder().userId(user.getUserId()).orderDetails(List.of(detail1, detail2)).build();
            paymentFacade.orderPayment(orderDto);
        } catch (OrderFailedException e) {
            Product updatedProduct1 = productJpaRepository.findById(product1.getProductId()).orElseThrow();
            Product updatedProduct2 = productJpaRepository.findById(product2.getProductId()).orElseThrow();

            assertEquals(product1.getStock(), updatedProduct1.getStock());
            assertEquals(product2.getStock(), updatedProduct2.getStock());
        }
    }

    @Test
    @DisplayName("주문 동시 요청 시 데드락 발생 하는지 테스트")
    void deadLockTest() throws InterruptedException {
        for (int i = 1; i <= 4; i++) {
            userJpaRepository.saveAndFlush(User.builder().userId((long)i).name("test" + i).point(BigDecimal.valueOf(10000)).build());
        }

        int concurrentRequests = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);

        OrderDetailDto detail1 = OrderDetailDto.builder().productId(product1.getProductId()).quantity(1).build();
        OrderDetailDto detail2 = OrderDetailDto.builder().productId(product2.getProductId()).quantity(1).build();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 1; i <= concurrentRequests; i++) {
            List<OrderDetailDto> orderDetails = (i % 2 == 0) ? List.of(detail1, detail2) : List.of(detail2, detail1);
            OrderDto orderDto = OrderDto.builder().userId((long) i).orderDetails(orderDetails).build();

            tasks.add(() -> {
                paymentFacade.orderPayment(orderDto);
                return null;
            });
        }

        List<Future<Void>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();

        assertDoesNotThrow(() -> {
            for (Future<Void> future : futures) {
                future.get();
            }
        });
    }

}
