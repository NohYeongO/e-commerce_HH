package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.data.DataPlatform;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
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

            orderDetails = productStockService.stockDeduction(orderDto);
            UserDto user = findUserService.getUser(orderDto.getUserId());
            responseOrder = orderService.orderPayment(user, orderDetails);
            priceDeductionService.priceDeductionSave(responseOrder.getUser());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            // 예외 발생시 재고 추가
            if (!orderDetails.isEmpty()) {
                productStockService.addStock(orderDetails);
            }
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        } finally {
            if (lockAcquired && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        dataPlatform.sendOrderData(responseOrder);
        return responseOrder;
    }
}
