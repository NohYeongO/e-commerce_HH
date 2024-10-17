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

    @Transactional
    public OrderDto orderPayment(UserDto userDto, List<ProductDto> productsDto, List<OrderDetailDto> orderDetails) {

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(OrderDetailDto orderDetail : orderDetails) {

            ProductDto productDto = productsDto.stream()
                    .filter(p -> p.getProductId().equals(orderDetail.getProductId())).findFirst().orElseThrow(() -> new ProductNotFoundException("주문할 수 없는 상품이 포함되어있습니다.", 404));

            Product product = productDto.toEntity();

            // 재고 수량 차감
            product.deduction(orderDetail.getQuantity());

            // 주문 수량 만큼 곱하기
            totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(orderDetail.getQuantity())));

            // 상세 주문 내역 생성
            OrderDetail Detail = OrderDetail.builder()
                    .product(product)
                    .quantity(orderDetail.getQuantity())
                    .build();
            // 상세주문 내역 리스트 추가
            orderDetailList.add(Detail);
            productJpaRepository.saveAndFlush(product);
        }

        User user = userDto.toEntity();

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderDetails(orderDetailList)
                .build();
        Order saveOrder = orderJpaRepository.save(order);
        // 금액 차감
        user.deduction(totalPrice);

        userJpaRepository.save(user);

        return OrderDto.builder()
                .orderId(saveOrder.getOrderId())
                .userId(user.getUserId())
                .orderDetails(orderDetailList.stream()
                        .map(od -> OrderDetailDto.builder()
                                .detailId(od.getDetailId())
                                .productId(od.getProduct().getProductId())
                                .quantity(od.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .orderDate(saveOrder.getOrderDate())
                .build();
    }
}
