package io.hhplus.ecommerce.domain.service.cart;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.entity.cart.Cart;
import io.hhplus.ecommerce.infra.cart.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindCartService {

    private final CartJpaRepository cartJpaRepository;

    // 장바구니 조회
    public List<CartDto> getCartItems(UserDto userDto) {
        List<Cart> carts = cartJpaRepository.findByUser_UserId(userDto.getUserId());
        return carts.stream()
                .map(CartDto::toDto)
                .toList();
    }

}
