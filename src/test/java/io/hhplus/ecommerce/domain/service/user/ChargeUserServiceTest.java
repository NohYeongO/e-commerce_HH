package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.api.request.ChargeRequest;
import io.hhplus.ecommerce.common.exception.ChargeFailedException;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class ChargeUserServiceTest {

    @InjectMocks
    ChargeUserService chargeUserService;

    @Mock
    UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("충전금액이 잘 충전되는지 테스트")
    void chargeTest() {
        // given
        Long userId = 1L;
        BigDecimal point = BigDecimal.valueOf(10000);
        ChargeRequest chargeRequest = new ChargeRequest(userId, point);

        BigDecimal mockPoint = BigDecimal.valueOf(30000);
        User mockUser = User.builder().userId(userId).name("test").point(mockPoint).build();

        // when
        when(userJpaRepository.findByUserId(userId)).thenReturn(Optional.ofNullable(mockUser));
        UserDto responseUser = chargeUserService.charge(chargeRequest);
        // then
        // 충전금액이 잘 충전됐는지 테스트
        assertEquals(responseUser.getPoint(), point.add(mockPoint));
    }

}