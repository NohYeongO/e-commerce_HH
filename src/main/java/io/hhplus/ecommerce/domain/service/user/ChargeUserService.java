package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ChargeUserService {

    private static final Logger log = LoggerFactory.getLogger(ChargeUserService.class);
    private final UserJpaRepository userJpaRepository;

    /**
     * 충전 기능
     */
    @Transactional
    public UserDto charge(ChargeRequest request) {
        User user = userJpaRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        user.addPoint(request.getPoint());

        userJpaRepository.save(user);

        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .point(user.getPoint()).build();
    }


}
