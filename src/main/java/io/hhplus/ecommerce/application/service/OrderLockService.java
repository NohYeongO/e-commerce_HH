package io.hhplus.ecommerce.application.service;

import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrderLockService {

    private final PaymentFacade paymentFacade;
    private final RedissonClient redissonClient;

    /**
     * Lock을 먼저 흭득하고 주문결제 기능 구현될수 있게
     */
    public OrderDto orderPayment(OrderDto orderDto) {
        String lockKey = "user:" + orderDto.getUserId();
        RLock rLock = redissonClient.getLock(lockKey);
        boolean lockAcquired = false;

        try {
            // 최대 3번까지 시도
            for (int attempt = 0; attempt < 3; attempt++) {
                lockAcquired = rLock.tryLock(10, 5, TimeUnit.SECONDS);
                if (lockAcquired) {
                    break;
                }
                Thread.sleep(100); // 잠금을 얻지 못했을 경우 대기
            }

            if (!lockAcquired) {
                throw new OrderFailedException(ErrorCode.ORDER_FAILED);
            }

            // 트랜잭션 적용된 메소드 호출
            return paymentFacade.orderPayment(orderDto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        } finally {
            if (lockAcquired) {
                rLock.unlock(); // 잠금 해제
            }
        }
    }
}
