package io.hhplus.swagger.api;


import io.hhplus.ecommerce.api.user.request.ChargeRequest;
import io.hhplus.ecommerce.domain.user.UserDto;
import io.hhplus.swagger.response.UserMockDto;
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


@RestController("/user")
public class UserMockAPI {

    @Operation(summary = "충전 API", description = "사용자의 포인트를 충전하고 충전된 금액과 사용자정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "충전 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserMockDto.class),
                            examples = @ExampleObject(value = "{\"userId\":\"user123\", \"username\":\"노영오\", \"point\":1500}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음", content = @Content)
    })
    @PostMapping("/charge")
    public ResponseEntity<UserMockDto> charge(@RequestBody ChargeRequest chargeRequest) {

        if (chargeRequest.getUserId() == null) {
            return ResponseEntity.status(404).build();  // 404 응답
        }
        // 가짜 UserMockDto 생성
        UserMockDto user = new UserMockDto();

        return ResponseEntity.ok(user);  // 200 응답
    }
}
