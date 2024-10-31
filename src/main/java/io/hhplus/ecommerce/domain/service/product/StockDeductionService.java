package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.domain.entity.order.OrderDetail;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockDeductionService {

    private static final Logger log = LoggerFactory.getLogger(StockDeductionService.class);
    private final ProductJpaRepository productJpaRepository;

    public List<OrderDetailDto> stockDeduction(OrderDto ordersDto) {
        // 상세주문 내역을 저장할 리스트
        List<OrderDetail> orderDetailList = new ArrayList<>();

        for(OrderDetailDto orderDetail : ordersDto.getOrderDetails()) {
            // 상품조회 비관적락 적용
            Product product = productJpaRepository.findByIdWithLock(orderDetail.getProductId()).orElseThrow(() -> {
                log.error("Product Not Found: {}", orderDetail.getProductId());
                return new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
            });
            // 재고차감
            product.deduction(orderDetail.getQuantity());
            // 상세주문내역 생성
            OrderDetail Detail = OrderDetail.builder()
                    .product(product)
                    .quantity(orderDetail.getQuantity())
                    .build();

            // 상세주문내역리스트에 저장
            orderDetailList.add(Detail);
        }
        return orderDetailList.stream().map(OrderDetailDto::toDto).toList();
    }
}
