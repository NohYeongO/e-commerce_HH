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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindUserService {
    private static final Logger log = LoggerFactory.getLogger(FindUserService.class);
    private final UserJpaRepository userJpaRepository;


    /**
     * 회원 조회
     */
    @Transactional
    public UserDto getUser(Long userId) {
        User user = userJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint()).build();
    }

}
