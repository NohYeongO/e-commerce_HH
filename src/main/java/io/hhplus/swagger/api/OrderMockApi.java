package io.hhplus.swagger.api;

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
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController("/order")
public class OrderMockApi {
    @Operation(summary = "주문 및 결제 API", description = "여러 상품을 주문하고 결제를 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 및 결제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderMockDto.class),
                            examples = @ExampleObject(value = "{\"orderId\":1, \"userId\":\"user123\", \"products\":[{\"productId\":1, \"productName\":\"Laptop\", \"price\":1000.00, \"quantity\":2}], \"orderDate\":\"2024-10-11T12:34:56\"}")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잔액 부족", content = @Content)
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderMockDto> placeOrder(@RequestBody OrderMockDto orderRequest) {
        // 사용자 잔액 예시
        BigDecimal userBalance = new BigDecimal("5000.00");
        // 총 주문 금액 계산
        BigDecimal totalOrderAmount = orderRequest.getProducts().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(5)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 잔액 부족 시 400 응답
        if (userBalance.compareTo(totalOrderAmount) < 0) {
            return ResponseEntity.status(400).build();
        }
        // 주문 처리 로직 (잔액 차감, 데이터 저장 등)
        return ResponseEntity.ok(orderRequest);  // 200 성공 응답
    }


}
