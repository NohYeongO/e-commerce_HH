package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.data.DataPlatform;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.common.exception.ChargeFailedException;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.service.order.OrderService;
import io.hhplus.ecommerce.domain.service.product.StockDeductionService;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.service.user.PriceDeductionService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final ChargeUserService chargeUserService;
    private final StockDeductionService stockDeductionService;
    private final PriceDeductionService priceDeductionService;
    private final FindUserService findUserService;
    private final RedissonClient redissonClient;

    private final DataPlatform dataPlatform;

    /**
     * 잔액 충전 / 조회 기능
     */
    public UserDto charge(ChargeRequest request) {
        String lockKey = "user:" + request.getUserId();
        RLock rLock = redissonClient.getLock(lockKey);
        boolean lockAcquired = false;

        try {
            // 최대 3번 시도
            for (int attempt = 0; attempt < 3; attempt++) {
                lockAcquired = rLock.tryLock(10, 5, TimeUnit.SECONDS);
                if (lockAcquired) {
                    break;
                }
                // 잠금을 얻지 못했을 경우 대기
                Thread.sleep(100);
            }

            if (!lockAcquired) {
                throw new ChargeFailedException(ErrorCode.CHARGE_FAILED);
            }

            // 잔액 충전
            return chargeUserService.charge(request);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChargeFailedException(ErrorCode.CHARGE_FAILED);
        } finally {
            if (lockAcquired) {
                rLock.unlock(); // 잠금 해제
            }
        }
    }

    /**
     * 주문 결제 기능
     */
    @Transactional
    public OrderDto orderPayment(OrderDto orderDto) {

        OrderDto ResponseOrder;
        try {
            // 회원조회
            UserDto user = findUserService.getUser(orderDto.getUserId());
            // 재고차감
            List<OrderDetailDto> orderDetails = stockDeductionService.stockDeduction(orderDto);
            // 최종주문
            ResponseOrder = orderService.orderPayment(user, orderDetails);
            // 잔고차감
            priceDeductionService.priceDeduction(user, ResponseOrder.getTotalPrice());
            // 외부 데이터 플랫폼 요청
            dataPlatform.sendOrderData(ResponseOrder);
        } catch (NullPointerException e){
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        }
        return ResponseOrder;
    }
}
