package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.data.DataPlatform;
import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.domain.service.order.OrderService;
import io.hhplus.ecommerce.domain.service.product.ProductService;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionFacade {

    private final OrderService orderService;
    private final FindUserService findUserService;
    private final ChargeUserService chargeUserService;
    private final ProductService productService;
    private final DataPlatform dataPlatform;
    /**
     * 잔액 충전 / 조회 기능
     */
    public UserDto charge(ChargeRequest request) {

        // 기존 회원 잔액 조회
        UserDto userDto = findUserService.getUser(request.getUserId(), true);

        // 충전
        User user = userDto.toEntity();
        user.addPoint(request.getPoint());

        // 데이터베이스 저장 요청
        return chargeUserService.charge(user);
    }

    /**
     * 주문 결제 기능
     */
    @Transactional
    public OrderDto orderPayment(OrderDto orderDto) {

        // 회원 조회
        UserDto user = findUserService.getUser(orderDto.getUserId(), true);
        // 상품 조회
        List<ProductDto> products = productService.getProducts(orderDto.getOrderDetails().stream().map(OrderDetailDto::getProductId).toList());
        // 주문
        OrderDto order = orderService.orderPayment(user, products, orderDto.getOrderDetails());

        // 외부 데이터 플랫폼 요청
        dataPlatform.sendOrderData(order);

        return order;
    }
}
