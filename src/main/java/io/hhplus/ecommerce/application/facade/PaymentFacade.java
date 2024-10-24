package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.data.DataPlatform;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.domain.service.order.OrderService;
import io.hhplus.ecommerce.domain.service.product.StockDeductionService;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.service.user.PriceDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final FindUserService findUserService;
    private final ChargeUserService chargeUserService;
    private final StockDeductionService stockDeductionService;
    private final PriceDeductionService priceDeductionService;


    private final DataPlatform dataPlatform;
    /**
     * 잔액 충전 / 조회 기능
     */
    public UserDto charge(ChargeRequest request) {
        // 기존 회원 잔액 조회
        UserDto userDto = findUserService.getUser(request.getUserId(), true);
        return chargeUserService.charge(userDto, request.getPoint());
    }

    /**
     * 주문 결제 기능
     */
    @Transactional
    public OrderDto orderPayment(OrderDto orderDto) {

        OrderDto ResponseOrder;
        try {
            // 회원조회
            UserDto user = findUserService.getUser(orderDto.getUserId(), true);
            // 재고차감
            List<OrderDetailDto> orderDetails = stockDeductionService.stockDeduction(orderDto);
            // 최종주문
            ResponseOrder = orderService.orderPayment(user, orderDetails);
            // 잔고차감
            priceDeductionService.priceDeduction(user, ResponseOrder.getTotalPrice());

            // 외부 데이터 플랫폼 요청
            dataPlatform.sendOrderData(ResponseOrder);
        } catch (NullPointerException e){
            throw new ResourceNotFoundException(ErrorCode.INVALID_REQUEST);
        }

        return ResponseOrder;
    }
}
