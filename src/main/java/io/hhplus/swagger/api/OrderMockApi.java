package io.hhplus.swagger.api;

import io.hhplus.ecommerce.application.dto.order.OrderDetailDto;
import io.hhplus.ecommerce.application.dto.order.OrderDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.swagger.response.OrderMockDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderMockApi {
    // 상품 가격은 ProductDto에서 가져온다고 가정
    private List<ProductDto> mockProducts = Arrays.asList(
            ProductDto.builder().productId(1L).name("Laptop").price(BigDecimal.valueOf(100000)).build(),
            ProductDto.builder().productId(2L).name("Smartphone").price(BigDecimal.valueOf(100000)).build()
    );

    /**
     * Mock 주문 및 결제 API
     */
    @Operation(summary = "주문 및 결제 API", description = "여러 상품을 주문하고 결제를 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 및 결제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class),
                            examples = @ExampleObject(value = "{\"orderId\":1, \"userId\":\"user123\", \"products\":[{\"productId\":1, \"productName\":\"Laptop\", \"quantity\":2}], \"orderDate\":\"2024-10-11T12:34:56\"}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잔액 부족", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDto> orderPayment(@RequestBody OrderDto orderRequest) {
        // 사용자 잔액 예시 (Mock 데이터)
        BigDecimal userBalance = new BigDecimal("5000.00");

        // 총 주문 금액 계산: 상품의 가격 정보를 ProductDto에서 가져옴
        BigDecimal totalOrderAmount = orderRequest.getOrderDetails().stream()
                .map(item -> {
                    // 상품 ID에 해당하는 가격 찾기
                    BigDecimal price = mockProducts.stream()
                            .filter(product -> product.getProductId().equals(item.getProductId()))
                            .findFirst()
                            .map(ProductDto::getPrice)
                            .orElse(BigDecimal.ZERO);
                    // 상품 가격 * 수량
                    return price.multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 잔액 부족 시 400 응답
        if (userBalance.compareTo(totalOrderAmount) < 0) {
            return ResponseEntity.status(400).build();  // 잔액 부족
        }

        // Mock 데이터 생성 및 반환
        List<OrderDetailDto> mockOrderDetails = Arrays.asList(
                OrderDetailDto.builder()
                        .detailId(1L)
                        .productId(1L)
                        .quantity(2)
                        .build()
        );

        OrderDto mockOrder = OrderDto.builder()
                .orderId(1L)
                .userId(123L)
                .orderDetails(mockOrderDetails)
                .orderDate(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(mockOrder);  // 200 성공 응답
    }
}
