package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindUserService {
    private static final Logger log = LoggerFactory.getLogger(FindUserService.class);
    private final UserJpaRepository userJpaRepository;

    /**
     * 회원 조회
     */
    public UserDto getUser(Long userId, boolean locked) {
        User user;
        if(locked){
            // Lock이 필요할 경우
            user = userJpaRepository.findByIdWithLock(userId).orElseThrow(() -> {
                        log.error("get User Not Found : userId={}", userId);
                        throw new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
                    });
        } else{
            // 단순 회원 조회 시
            user = userJpaRepository.findById(userId).orElseThrow(() -> {
                        log.error("get User Not Found : userId={}", userId);
                        throw new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
                    });
        }
        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint()).build();
    }

}
