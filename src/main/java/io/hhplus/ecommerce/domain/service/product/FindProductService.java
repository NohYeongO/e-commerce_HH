package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FindProductService {
    private static final Logger log = LoggerFactory.getLogger(FindProductService.class);
    private final ProductJpaRepository productJpaRepository;

    /**
     * 특정 상품 조회
     */
    public ProductDto getProduct(Long productId){

        Product product = productJpaRepository.findById(productId).orElseThrow(() -> {
                    log.error("get Product Data Null: {}", productId);
                    return new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
                });

        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    /**
     * 최근 3일간 판매 상위 5개 상품 조회
     */
    public List<ProductDto> getTopFiveProducts() {
        // 최근 3일의 시작 날짜 계산 00시 기준
        LocalDateTime startDate = LocalDate.now().minusDays(3).atStartOfDay();

        List<Product> topProducts = productJpaRepository.findTop5Product(startDate);

        if (topProducts == null || topProducts.isEmpty()) {
            log.error("get Top Five Products Data Null or Empty");
            throw new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return topProducts.stream()
                .map(product -> ProductDto.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .build())
                .toList();
    }
}
