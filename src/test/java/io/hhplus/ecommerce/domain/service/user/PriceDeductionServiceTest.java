package io.hhplus.ecommerce.domain.service.user;

import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PriceDeductionServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private PriceDeductionService priceDeductionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("잔고가 충분할 때 정상적으로 차감")
    void priceDeductionSuccess() {
        // given: 초기 잔고가 5000인 사용자
        BigDecimal initialBalance = BigDecimal.valueOf(5000);
        BigDecimal deductionPoint = BigDecimal.valueOf(1000);
        User user = new User(1L, "testUser", initialBalance);
        UserDto userDto = UserDto.builder().userId(1L).name("testUser").point(initialBalance).build();

        // when: priceDeduction이 호출되고 잔고가 차감됨
        when(userJpaRepository.save(any(User.class))).thenReturn(user);

        // 실제 테스트 실행
        priceDeductionService.priceDeduction(userDto, deductionPoint);

        // then: ArgumentCaptor를 사용하여 저장된 User 객체를 캡처
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userJpaRepository).save(userCaptor.capture());

        // 캡처된 User 객체의 point 값 검증
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPoint()).isEqualTo(initialBalance.subtract(deductionPoint));
    }

    @Test
    @DisplayName("잔고가 부족할 때 IllegalArgumentException이 발생")
    void insufficientBalanceThrowsException() {
        // given: 차감할 잔고보다 적은 초기 잔고 (500원)
        BigDecimal initialBalance = BigDecimal.valueOf(500);
        BigDecimal deductionAmount = BigDecimal.valueOf(1000);  // 차감하려는 금액이 1000
        User user = new User(1L, "testUser", initialBalance);
        UserDto userDto = UserDto.builder().userId(1L).name("testUser").point(initialBalance).build();

        // then: 예외가 발생하는지 확인
        assertThatThrownBy(() -> priceDeductionService.priceDeduction(userDto, deductionAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("포인트가 부족합니다.");

        // 잔고 부족으로 save가 호출되지 않는지 확인
        verify(userJpaRepository, never()).save(any(User.class));
    }
}