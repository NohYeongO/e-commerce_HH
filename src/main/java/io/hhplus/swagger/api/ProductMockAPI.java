package io.hhplus.swagger.api;

import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.swagger.response.ProductMockDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductMockAPI {

    /**
     * Mock 상품 조회 API
     */
    @Operation(summary = "상품 조회 API", description = "상품을 ID로 조회하고, 상품 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = "{\"productId\":1, \"productName\":\"Laptop\", \"price\":1000.00, \"stock\":50}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("id") Long productId) {
        // 가짜 상품 목록 생성
        ProductDto product1 = ProductDto.builder().productId(1L).name("Laptop").price(BigDecimal.valueOf(100000)).build();
        ProductDto product2 = ProductDto.builder().productId(2L).name("Smartphone").price(BigDecimal.valueOf(100000)).build();
        List<ProductDto> productList = Arrays.asList(product1, product2);

        // ID에 맞는 상품을 찾음
        ProductDto product = productList.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        // 상품이 없을 경우 404 반환
        if (product == null) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(product);  // 성공적으로 상품 반환
    }

    /**
     * Mock 상위 5개 상품 조회 API
     */
    @Operation(summary = "상위 5개 상품 조회 API", description = "재고가 있는 상위 5개의 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = "[{\"productId\":1, \"productName\":\"Laptop\", \"price\":1000.00, \"stock\":50},{\"productId\":2, \"productName\":\"Smartphone\", \"price\":500.00, \"stock\":30}]")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/top5")
    public ResponseEntity<List<ProductDto>> getTop5Products() {
        // 가짜 상품 목록 생성
        ProductDto product1 = ProductDto.builder().productId(1L).name("Laptop").price(BigDecimal.valueOf(100000)).build();
        ProductDto product2 = ProductDto.builder().productId(2L).name("Smartphone").price(BigDecimal.valueOf(100000)).build();
        ProductDto product3 = ProductDto.builder().productId(3L).name("Tablet").price(BigDecimal.valueOf(100000)).build();
        ProductDto product4 = ProductDto.builder().productId(4L).name("Headphones").price(BigDecimal.valueOf(100000)).build();
        ProductDto product5 = ProductDto.builder().productId(5L).name("Monitor").price(BigDecimal.valueOf(100000)).build();
        ProductDto product6 = ProductDto.builder().productId(6L).name("Keyboard").price(BigDecimal.valueOf(100000)).build();

        List<ProductDto> productList = Arrays.asList(product1, product2, product3, product4, product5, product6);

        // 재고가 있는 상품만 필터링하고 상위 5개만 반환
        List<ProductDto> top5Products = productList.stream()
                .filter(p -> p.getStock() > 0)  // 재고가 있는 상품만
                .limit(5)  // 상위 5개 상품만 반환
                .collect(Collectors.toList());

        // 상위 5개 상품이 없을 경우 404 오류 반환
        if (top5Products.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(top5Products);  // 성공적으로 상위 5개 상품 반환
    }

}
