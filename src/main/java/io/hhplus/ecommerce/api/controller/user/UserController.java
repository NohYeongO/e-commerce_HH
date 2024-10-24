package io.hhplus.ecommerce.api.controller.user;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.facade.PaymentFacade;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final PaymentFacade paymentFacade;

    /**
     * 잔액 충전 / 조회 API
     */
    @PostMapping("/charge")
    public ResponseEntity<UserDto> charge(@RequestBody ChargeRequest chargeRequest) {

        UserDto user = paymentFacade.charge(chargeRequest);

        return ResponseEntity.ok(user);
    }
}
