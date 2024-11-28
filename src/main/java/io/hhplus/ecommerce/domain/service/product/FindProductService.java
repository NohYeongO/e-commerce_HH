package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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
     * 최근 3일간 판매 상위 5개 상품을 캐시에서 조회하고, 없으면 DB에서 조회 후 캐시에 저장
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "top5ProductsCache", key = "'topfiveproduct'")
    public List<ProductDto> getTopFiveProducts() {
        // DB에서 최근 3일간의 상위 5개 상품 조회
        LocalDateTime startDate = LocalDate.now().minusDays(3).atStartOfDay();
        Page<Product> topProducts = productJpaRepository.findTop5Product(startDate, PageRequest.of(0, 5));
        List<Product> top5Products = topProducts.getContent();

        if (top5Products.isEmpty()) {
            log.warn("No top products found in the last 3 days");
            return Collections.emptyList();
        }

        return top5Products.stream()
                .map(product -> ProductDto.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 매일 자정에 상위 5개 상품 캐시 갱신
     */
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정 00시에 실행
    @CachePut(value = "top5ProductsCache", key = "'topfiveproduct'")
    public List<ProductDto> cacheTopFiveProducts() {
        LocalDateTime startDate = LocalDate.now().minusDays(3).atStartOfDay();
        Page<Product> topProducts = productJpaRepository.findTop5Product(startDate, PageRequest.of(0, 5));
        List<Product> top5Products = topProducts.getContent();

        if (top5Products.isEmpty()) {
            log.warn("No top products found in the last 3 days for cache update");
            return Collections.emptyList();
        }

        return top5Products.stream()
                .map(product -> ProductDto.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .build())
                .collect(Collectors.toList());
    }


}
