package io.hhplus.ecommerce.concurrency;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class OrderAndChargeTest extends IntegrationTestSupport {

    private static final Logger log = LoggerFactory.getLogger(OrderAndChargeTest.class);
    private Long userId;
    private Long productId1;
    private Long productId2;

    @BeforeEach
    void setup() {
        User user = userJpaRepository.save(User.builder().name("영오").point(BigDecimal.valueOf(10000)).build());
        Product product1 = productJpaRepository.save(Product.builder().name("ice").price(BigDecimal.valueOf(5000)).stock(10).build());
        Product product2 = productJpaRepository.save(Product.builder().name("milk").price(BigDecimal.valueOf(5000)).stock(10).build());

        userId = user.getUserId();
        productId1 = product1.getProductId();
        productId2 = product2.getProductId();
        log.info("회원 ID: {}", userId);
        User existingUser = userJpaRepository.findById(user.getUserId()).orElse(null);
    }

    @Test
    void testOrderPaymentAndChargeConcurrency() throws ExecutionException, InterruptedException {
        // 주문과 충전에 필요한 요청 데이터 준비
        OrderDetailDto detail1 = OrderDetailDto.builder().productId(productId1).quantity(1).build();
        OrderDetailDto detail2 = OrderDetailDto.builder().productId(productId2).quantity(1).build();
        List<OrderDetailDto> orderDetails = List.of(detail1, detail2);

        OrderDto orderDto = OrderDto.builder().userId(userId).orderDetails(orderDetails).build();
        ChargeRequest chargeRequest = new ChargeRequest(userId, BigDecimal.valueOf(10000));

        // 주문과 충전 작업을 동시에 수행
        AtomicBoolean orderSuccess = new AtomicBoolean(false);
        AtomicBoolean chargeSuccess = new AtomicBoolean(false);

        CompletableFuture<Void> orderFuture = CompletableFuture.runAsync(() -> {
            try {
                OrderDto order = orderLockService.orderPayment(orderDto);
                orderSuccess.set(true);
            } catch (Exception e) {
                log.error("Order failed: {}", e.getMessage());
            }
        });

        CompletableFuture<Void> chargeFuture = CompletableFuture.runAsync(() -> {
            try {
                UserDto user = paymentFacade.charge(chargeRequest);
                chargeSuccess.set(true);
            } catch (Exception e) {
                log.error("Charge failed: {}", e.getMessage());
            }
        });

        // 두 작업이 완료되기를 기다림
        CompletableFuture.allOf(orderFuture, chargeFuture).join();

        // 재고와 유저 포인트가 예상대로 변경되었는지 확인
        User updatedUser = userJpaRepository.findById(userId).orElseThrow();
        Product updatedProduct1 = productJpaRepository.findById(productId1).orElseThrow();
        Product updatedProduct2 = productJpaRepository.findById(productId2).orElseThrow();

        log.info("Product 1 남은 재고: {}", updatedProduct1.getStock());
        log.info("Product 2 남은 재고: {}", updatedProduct2.getStock());
        log.info("회원 잔액: {}", updatedUser.getPoint());

        // 재고와 포인트가 올바르게 차감되었는지 검증
        assertEquals(9, updatedProduct1.getStock(), "Product 1 재고 검증");
        assertEquals(9, updatedProduct2.getStock(), "Product 2 재고 검증");
        assertEquals(BigDecimal.valueOf(10000), updatedUser.getPoint(), "회원 포인트 검증");
    }

}
