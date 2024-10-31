package io.hhplus.ecommerce.application.facade;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.dto.user.UserDto;
import io.hhplus.ecommerce.domain.service.cart.FindCartService;
import io.hhplus.ecommerce.domain.service.cart.UpdateCartService;
import io.hhplus.ecommerce.domain.service.user.FindUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final FindCartService findCartService;
    private final UpdateCartService updateCartService;
    private final FindUserService findUserService;

    // 장바구니 상품 추가
    @Transactional
    public void addCart(CartDto cartDto) {
        // 회원 조회 및 장바구니 상품 추가 로직
        UserDto user = findUserService.getUser(cartDto.getUserId());
        CartDto cart = CartDto.builder().user(user).products(cartDto.getProducts()).build();
        updateCartService.addCart(cart);
    }

    // 장바구니 상품 삭제
    @Transactional
    public void removeCart(Long cartId) {
        updateCartService.removeCart(cartId);
    }

    // 장바구니 조회
    public List<CartDto> getCartItems(Long userId) {
        // 사용자 조회 후 장바구니 조회
        UserDto user = findUserService.getUser(userId);
        return findCartService.getCartItems(user);
    }
}
