package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.common.exception.user.UserChargeFailedException;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeUserService {

    private final UserJpaRepository userJpaRepository;

    /**
     * 충전 기능
     */
    @Transactional
    public UserDto charge(User user){
        try {
            user = userJpaRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("무결성 제약 오류 발생 = {}", e.getMessage());
            throw new UserChargeFailedException("충전에 실패했습니다. 잠시 후 다시 시도해주세요.");
        } catch (Exception e) {
            log.error("충전 중 오류발생 = {}", e.getMessage());
            throw new UserChargeFailedException("충전에 실패했습니다. 잠시 후 다시 시도해주세요");
        }
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint())
                .build();
    }
}
