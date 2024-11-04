package io.hhplus.ecommerce.domain.service.order;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.entity.order.Order;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderJpaRepository orderJpaRepository;

    @Transactional
    public OrderDto orderPayment(UserDto userDto, List<OrderDetailDto> orderDetailDtoList) {

        User user = userDto.toEntity();
        // 총 가격을 계산
        BigDecimal totalPrice = orderDetailDtoList.stream()
                .map(detail -> detail.getProductDto().getPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderDetails(orderDetailDtoList.stream().map(detail -> OrderDetail.builder()
                        .product(detail.getProductDto().toEntity())
                        .quantity(detail.getQuantity()).build()).toList()
                )
                .build();
        try {
            // 데이터베이스에 저장
            Order savedOrder = orderJpaRepository.save(order);
            // 저장 성공 후 결과 반환
            return OrderDto.builder()
                    .orderId(savedOrder.getOrderId())
                    .userId(user.getUserId())
                    .orderDetails(savedOrder.getOrderDetails().stream()
                            .map(od -> OrderDetailDto.builder()
                                    .detailId(od.getDetailId())
                                    .productId(od.getProduct().getProductId())
                                    .quantity(od.getQuantity())
                                    .build())
                            .collect(Collectors.toList()))
                    .orderDate(savedOrder.getOrderDate())
                    .totalPrice(totalPrice)
                    .build();
        } catch (DataIntegrityViolationException e) {
            log.error("Order DataIntegrityViolationException: {}", e.getMessage());
            throw new OrderFailedException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        } catch (Exception e) {
            log.error("Order Exception: {}", e.getMessage());
            throw new OrderFailedException(ErrorCode.ORDER_FAILED);
        }
    }
}
