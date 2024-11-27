package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.data.DataPlatform;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.event.OrderFailedEvent;
import io.hhplus.ecommerce.application.event.OrderSuccessEvent;
import io.hhplus.ecommerce.common.exception.ChargeFailedException;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.service.order.OrderService;
import io.hhplus.ecommerce.domain.service.product.ProductStockService;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.service.user.PriceDeductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final ChargeUserService chargeUserService;
    private final ProductStockService productStockService;
    private final PriceDeductionService priceDeductionService;
    private final FindUserService findUserService;
    private final RedissonClient redissonClient;
    private final ApplicationEventPublisher eventPublisher;

    private final DataPlatform dataPlatform;

    public UserDto charge(ChargeRequest request) {
        String lockKey = "user:" + request.getUserId();
        RLock rLock = redissonClient.getLock(lockKey);
        boolean lockAcquired = false;
        UserDto userDto = null;
        try {
            lockAcquired = rLock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                throw new ChargeFailedException(ErrorCode.CHARGE_FAILED);
            }
            userDto = chargeUserService.charge(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChargeFailedException(ErrorCode.CHARGE_FAILED);
        } finally {
            if (lockAcquired && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("충전 Lock 해제: {}", LocalDateTime.now());
            }
        }
        return userDto;
    }

    public OrderDto orderPayment(OrderDto orderDto) {
        String lockKey = "user:" + orderDto.getUserId();
        RLock rLock = redissonClient.getLock(lockKey);

        boolean lockAcquired = false;
        OrderDto responseOrder = null;
        List<OrderDetailDto> orderDetails = new ArrayList<>();

        try {
            lockAcquired = rLock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                throw new OrderFailedException(ErrorCode.ORDER_FAILED);
            }
            // 1. 재고 차감
            orderDetails = productStockService.stockDeduction(orderDto);
            // 2. 유저 정보 조회
            UserDto user = findUserService.getUser(orderDto.getUserId());
            // 3. 주문 생성 및 잔고 차감
            responseOrder = orderService.orderPayment(user, orderDetails);
            // 4. 잔고 차감데이터 DB 저장
            priceDeductionService.priceDeductionSave(responseOrder.getUser());
            // 5. 성공 시 이벤트 발행
            eventPublisher.publishEvent(new OrderSuccessEvent(responseOrder));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        } catch (Exception e) {
            if (!orderDetails.isEmpty()) {
                // 예외 발생 시 보상 트랜잭션 이벤트 발행
                eventPublisher.publishEvent(new OrderFailedEvent(orderDto, e.getMessage()));
            } else{
                log.error("예외 발생: {}", e.getMessage());
            }
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        } finally {
            if (lockAcquired && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("주문 Lock 해제: {}", LocalDateTime.now());
            }
        }
        return responseOrder;
    }
}
