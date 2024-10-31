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
    public UserDto getUser(Long userId) {
        User user = userJpaRepository.findByUserId(userId);

        log.info("제발 조히좀 돼라: {}", user.getUserId());

        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint()).build();
    }

}
