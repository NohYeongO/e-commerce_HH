package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.OrderFailedException;
import io.hhplus.ecommerce.domain.entity.user.User;
import io.hhplus.ecommerce.domain.service.user.PriceDeductionService;
import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import io.hhplus.ecommerce.infra.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductStockServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private PriceDeductionService priceDeductionService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용
        user = User.builder()
                .userId(1L)
                .name("Test User")
                .point(BigDecimal.valueOf(5000))
                .build();

        userDto = UserDto.toDto(user);
    }

    @Test
    @DisplayName("정상적인 잔고 차감 테스트")
    void priceDeductionTest() {
        // 차감할 금액
        BigDecimal price = BigDecimal.valueOf(1000);

        // ArgumentCaptor 설정
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // return Mock 객체 설정
        when(userJpaRepository.save(any(User.class))).thenReturn(user);

        // 테스트 실행
        priceDeductionService.priceDeduction(userDto, price);

        // save 메서드가 호출되었고, 호출된 객체를 캡처하여 포인트가 차감되었는지 확인
        verify(userJpaRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        // 차감 후 포인트 확인
        assertEquals(BigDecimal.valueOf(4000), capturedUser.getPoint());
    }

    @Test
    @DisplayName("잔고 차감 시 DataIntegrityViolationException 발생 테스트")
    void dataIntegrityViolationExceptionTest() {
        // 차감 금액
        BigDecimal price = BigDecimal.valueOf(1000);

        // DataIntegrityViolationException을 발생시키도록 설정
        when(userJpaRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("데이터 오류"));

        // OrderFailedException 예외 발생 여부 확인
        OrderFailedException exception = assertThrows(OrderFailedException.class, () ->
                priceDeductionService.priceDeduction(userDto, price)
        );
        // save 메서드가 호출된 것을 확인
        verify(userJpaRepository, times(1)).save(any(User.class));
    }



}