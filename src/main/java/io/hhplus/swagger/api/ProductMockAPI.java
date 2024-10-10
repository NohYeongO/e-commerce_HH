package io.hhplus.swagger.api;

import io.hhplus.swagger.response.ProductMockDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController("/product")
public class ProductMockAPI {

    @Operation(summary = "상품 조회 API", description = "상품 목록을 조회하고, 상품이 없는 경우 오류를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductMockDto.class),
                            examples = @ExampleObject(value = "[{\"productId\":1, \"productName\":\"Laptop\", \"price\":1000.00, \"stock\":50}]")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "400", description = "수량 부족", content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<List<ProductMockDto>> getProducts() {
        // 가짜 상품 목록 생성
        ProductMockDto product1 = new ProductMockDto(1L, "Laptop", new BigDecimal("1000.00"), 50);
        ProductMockDto product2 = new ProductMockDto(2L, "Smartphone", new BigDecimal("500.00"), 0);
        List<ProductMockDto> productList = Arrays.asList(product1, product2);
        // 상품이 없을 경우 404 오류 반환
        if (productList.isEmpty()) {
            return ResponseEntity.status(404).build();  // 404 상품 없음
        }
        // 재고가 0인 경우 400 오류 반환
        if (productList.stream().anyMatch(p -> p.getStock() <= 0)) {
            return ResponseEntity.status(400).build();  // 400 수량 부족 오류
        }
        return ResponseEntity.ok(productList);  // 200 성공 응답
    }

    @Operation(summary = "상위 5개 상품 조회 API", description = "재고가 있는 상위 5개의 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductMockDto.class),
                            examples = @ExampleObject(value = "[{\"productId\":1, \"productName\":\"Laptop\", \"price\":1000.00, \"stock\":50},{\"productId\":2, \"productName\":\"phone\", \"price\":1000.00, \"stock\":50}]")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/top5")
    public ResponseEntity<List<ProductMockDto>> getTop5Products() {
        // 가짜 상품 목록 생성
        ProductMockDto product1 = new ProductMockDto(1L, "Laptop", new BigDecimal("1000.00"), 50);
        ProductMockDto product2 = new ProductMockDto(2L, "Smartphone", new BigDecimal("500.00"), 30);
        ProductMockDto product3 = new ProductMockDto(3L, "Tablet", new BigDecimal("300.00"), 20);
        ProductMockDto product4 = new ProductMockDto(4L, "Headphones", new BigDecimal("100.00"), 15);
        ProductMockDto product5 = new ProductMockDto(5L, "Monitor", new BigDecimal("200.00"), 25);
        ProductMockDto product6 = new ProductMockDto(6L, "Keyboard", new BigDecimal("50.00"), 10);

        List<ProductMockDto> productList = Arrays.asList(product1, product2, product3, product4, product5, product6);

        // 재고가 있는 상품만 필터링하고 상위 5개만 반환
        List<ProductMockDto> top5Products = productList.stream()
                .filter(p -> p.getStock() > 0)  // 재고가 있는 상품만
                .limit(5)  // 상위 5개 상품만 반환
                .collect(Collectors.toList());

        // 상위 5개 상품이 없을 경우 404 오류 반환
        if (top5Products.isEmpty()) {
            return ResponseEntity.status(404).build();  // 404 상품 없음
        }

        return ResponseEntity.ok(top5Products);  // 성공적으로 상위 5개 상품 반환
    }

}
