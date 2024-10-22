package io.hhplus.ecommerce.domain.service.order;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.domain.entity.order.Order;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.order.OrderJpaRepository;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public OrderDto orderPayment(UserDto userDto, List<OrderDetailDto> orderDetailDtoList) {

        User user = userDto.toEntity();
        // 총 가격을 계산
        BigDecimal totalPrice = orderDetailDtoList.stream()
                .map(detail -> detail.getProductDto().getPrice().multiply(new BigDecimal(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderDetails(orderDetailDtoList.stream().map(detail -> OrderDetail.builder()
                        .product(detail.getProductDto().toEntity())
                        .quantity(detail.getQuantity()).build()).toList()
                )
                .build();

        Order saveOrder = orderJpaRepository.save(order);

        return OrderDto.builder()
                .orderId(saveOrder.getOrderId())
                .userId(user.getUserId())
                .orderDetails(order.getOrderDetails().stream()
                        .map(od -> OrderDetailDto.builder()
                                .detailId(od.getDetailId())
                                .productId(od.getProduct().getProductId())
                                .quantity(od.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .orderDate(saveOrder.getOrderDate())
                .totalPrice(totalPrice)
                .build();
    }
}
