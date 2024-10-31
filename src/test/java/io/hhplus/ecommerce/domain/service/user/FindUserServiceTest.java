package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.common.exception.ResourceNotFoundException;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.user.User;
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
    @DisplayName("회원이 존재할 경우")
    void userFound() {
        // given
        Long userId = 1L;
        String name = "test";
        BigDecimal point = BigDecimal.valueOf(1000.00);

        User user = User.builder().userId(userId).name(name).point(point).build();

        when(userJpaRepository.findByUserId(1L)).thenReturn(user);

        // when
        UserDto findUser = findUserService.getUser(userId);
        // then
        assertEquals(userId, findUser.getUserId());
        assertEquals(name, findUser.getName());
        assertEquals(point, findUser.getPoint());
    }

}