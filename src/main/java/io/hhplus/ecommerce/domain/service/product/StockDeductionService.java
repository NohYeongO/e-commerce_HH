package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockDeductionService {
    private static final Logger log = LoggerFactory.getLogger(StockDeductionService.class);
    private final ProductJpaRepository productJpaRepository;

    public List<OrderDetailDto> stockDeduction(OrderDto ordersDto) {

        // 주문 상품 아이디
        List<Long> productIds = ordersDto.getOrderDetails().stream().map(OrderDetailDto::getProductId).toList();
        // 상품 조회
        List<Product> products = productJpaRepository.findAllById(productIds);
        // 조회된 상품 수와 요청한 ID 수가 다르면 예외 발생
        if (products.size() != productIds.size()) {
            throw new ProductNotFoundException("조회할 수 없는 상품이 포함되어 있습니다.", 404);
        }
        List<OrderDetail> orderDetailList = new ArrayList<>();

        // Product를 productId를 키로 갖는 Map
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        for(OrderDetailDto orderDetail : ordersDto.getOrderDetails()) {

            Product product = productMap.get(orderDetail.getProductId());

            if (product == null) {
                throw new ProductNotFoundException("주문할 수 없는 상품이 포함되어 있습니다.", 404);
            }
            // 재고 수량 차감
            product.deduction(orderDetail.getQuantity());

            OrderDetail Detail = OrderDetail.builder()
                    .product(product)
                    .quantity(orderDetail.getQuantity())
                    .build();

            orderDetailList.add(Detail);
        }
        return orderDetailList.stream().map(OrderDetailDto::toDto).toList();
    }



}
