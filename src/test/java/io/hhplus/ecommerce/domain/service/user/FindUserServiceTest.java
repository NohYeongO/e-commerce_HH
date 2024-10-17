package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.common.exception.user.UserNotFoundException;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FindUserServiceTest {
    @InjectMocks
    FindUserService findUserService;

    @Mock
    UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원조회시 조회 회원이 존재하지 않을 경우")
    void userNotFound() {
        // given
        Long userId = 1L;
        // when
        when(userJpaRepository.findById(1L)).thenReturn(Optional.empty());
        when(userJpaRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());
        // then
        assertThrows(UserNotFoundException.class, () -> findUserService.getUser(userId, false));
        assertThrows(UserNotFoundException.class, () -> findUserService.getUser(userId, true));
    }

    @Test
    @DisplayName("회원이 존재할 경우")
    void userFound() {
        // given
        Long userId = 1L;
        String name = "test";
        BigDecimal point = BigDecimal.valueOf(1000.00);

        User user = User.builder().userId(userId).name(name).point(point).build();

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userJpaRepository.findByIdWithLock(userId)).thenReturn(Optional.of(user));

        // when
        UserDto findUser = findUserService.getUser(userId, false);
        UserDto findLockUser = findUserService.getUser(userId, true);
        // then
        assertEquals(userId, findUser.getUserId());
        assertEquals(userId, findLockUser.getUserId());

        assertEquals(name, findUser.getName());
        assertEquals(name, findLockUser.getName());

        assertEquals(point, findUser.getPoint());
        assertEquals(point, findLockUser.getPoint());
    }

}