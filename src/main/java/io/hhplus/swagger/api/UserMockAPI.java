package io.hhplus.swagger.api;


import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.swagger.request.ChargeRequest;
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

@RestController
@RequestMapping("/user")  // @RequestMapping을 통해 공통 경로 설정
public class UserMockAPI {


    /**
     * Mock 충전 API
     */
    @Operation(summary = "충전 API", description = "사용자의 포인트를 충전하고 충전된 금액과 사용자 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "충전 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class),
                            examples = @ExampleObject(value = "{\"userId\":\"user123\", \"username\":\"노영오\", \"point\":1500}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음", content = @Content)
    })
    @PostMapping("/charge")
    public ResponseEntity<UserDto> charge(@RequestBody ChargeRequest chargeRequest) {

        // 사용자 ID가 없으면 404 반환
        if (chargeRequest.getUserId() == null) {
            return ResponseEntity.status(404).build();
        }

        // Mock UserDto 생성 (가짜 데이터)
        UserDto mockUser = UserDto.builder()
                .userId(1L)
                .name("노영오")
                .point(BigDecimal.valueOf(10000))
                .build();

        // Mock 데이터를 200 OK로 반환
        return ResponseEntity.ok(mockUser);
    }
}
