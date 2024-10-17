package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.common.exception.user.UserChargeFailedException;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.domain.service.user.ChargeUserService;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("충전이 성공했을 경우")
    void chargeSuccess() {
        // given
        Long userId = 1L;
        String name = "test";
        BigDecimal point = BigDecimal.valueOf(1000.00);
        User user = User.builder().userId(userId).name(name).point(point).build();

        when(userJpaRepository.save(user)).thenReturn(user);

        // when
        UserDto userDto = chargeUserService.charge(user);

        // then
        assertNotNull(userDto);
        assertEquals(user.getUserId(), userDto.getUserId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getPoint(), userDto.getPoint());
    }

    @Test
    @DisplayName("충전 중 데이터 오류 발생시")
    void chargeDataExceptionFail() {
        // given
        Long userId = 1L;
        String name = "test";
        BigDecimal point = BigDecimal.valueOf(1000.00);
        User user = User.builder().userId(userId).name(name).point(point).build();
        // when
        when(userJpaRepository.save(user)).thenThrow(new DataIntegrityViolationException("무결성 제약 오류"));
        // then
        assertThrows(UserChargeFailedException.class, () -> {
            chargeUserService.charge(user);
        });
    }


}