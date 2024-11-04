package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PriceDeductionService {
    private static final Logger log = LoggerFactory.getLogger(PriceDeductionService.class);
    private final UserJpaRepository userJpaRepository;

    // 잔고차감데이터 저장
    @Transactional
    public void priceDeductionSave(UserDto userDto) {
        try{
            User user = userDto.toEntity();
            userJpaRepository.save(user);
        }catch (DataIntegrityViolationException e){
            log.error("price Deduction DB Error: {}", e.getMessage());
            throw new OrderFailedException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
    }

}
