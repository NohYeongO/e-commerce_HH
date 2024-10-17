package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.domain.entity.product.Product;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductJpaRepository productJpaRepository;

    /**
     * 특정 상품 조회
     */
    public ProductDto getProduct(Long productId){

        Product product = productJpaRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("조회된 상품이 없습니다.", 200));

        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    /**
     * 상품 여러개 조회
     */
    public List<ProductDto> getProducts(List<Long> productIds){
        List<Product> products = productJpaRepository.findAllById(productIds);

        // 조회된 상품 수와 요청한 ID 수가 다르면 예외 발생
        if (products.size() != productIds.size()) {
            throw new ProductNotFoundException("조회할 수 없는 상품이 포함되어 있습니다.", 404);
        }

        return products.stream().map(product -> ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build()).toList();
    }

    /**
     * 판매 상위 5개 상품 조회
     */
    public List<ProductDto> getTopFiveProducts() {
        return productJpaRepository.findTop5Product().stream().map(product -> ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build()).toList();
    }
}
