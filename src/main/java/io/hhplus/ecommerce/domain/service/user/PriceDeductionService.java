package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PriceDeductionService {

    private final UserJpaRepository userJpaRepository;

    // 잔고차감
    public void priceDeduction(UserDto userDto, BigDecimal price) {
        User user = userDto.toEntity();
        user.deduction(price);
        userJpaRepository.save(user);
    }



}
