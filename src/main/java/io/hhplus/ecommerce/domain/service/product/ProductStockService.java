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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStockService {

    private static final Logger log = LoggerFactory.getLogger(ProductStockService.class);
    private final ProductJpaRepository productJpaRepository;

    @Transactional
    public List<OrderDetailDto> stockDeduction(OrderDto ordersDto) {
        List<OrderDetail> orderDetailList = new ArrayList<>();
        try {
            // Comparator.comparingLong을 사용해 오름차순으로 productId 정렬
            List<OrderDetailDto> orderedDetails = ordersDto.getOrderDetails().stream()
                    .sorted(Comparator.comparingLong(OrderDetailDto::getProductId))
                    .toList();

            for (OrderDetailDto orderDetail : orderedDetails) {
                // 상품 조회 비관적 락 적용
                Product product = productJpaRepository.findByIdWithLock(orderDetail.getProductId())
                        .orElseThrow(() -> {
                            log.error("Product Not Found: {}", orderDetail.getProductId());
                            return new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
                        });
                // 재고 차감
                product.deduction(orderDetail.getQuantity());
                // 상세 주문 내역 생성
                OrderDetail detail = OrderDetail.builder()
                        .product(product)
                        .quantity(orderDetail.getQuantity())
                        .build();

                // 상세 주문 내역 리스트에 저장
                orderDetailList.add(detail);
            }
            return orderDetailList.stream().map(OrderDetailDto::toDto).toList();
        } catch (DataAccessException e) {
            log.error("예외발생 userId: {}, 예외종류: {}", ordersDto.getUserId(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void addStock(List<OrderDetailDto> orderDetails) {
        for(OrderDetailDto orderDetail : orderDetails) {
            Product product = productJpaRepository.findByIdWithLock(orderDetail.getProductDto().getProductId())
                    .orElseThrow(() -> {
                        log.error("Product Not Found: {}", orderDetail.getProductId());
                        return new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
                    });
            product.addStock(orderDetail.getQuantity());
            productJpaRepository.save(product);
        }
    }
}
