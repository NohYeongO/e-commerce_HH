package io.hhplus.ecommerce.api.controller.cart;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.facade.CartFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    // 장바구니 상품 추가 API
    @PostMapping("/add")
    public ResponseEntity<String> addCart(@RequestBody CartDto cartDto) {
        cartFacade.addCart(cartDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart successfully.");
    }

    // 장바구니 상품 삭제 API
    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> removeCart(@PathVariable Long cartId) {
        cartFacade.removeCart(cartId);
        return ResponseEntity.ok("Product removed from cart successfully.");
    }

    // 장바구니 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartDto>> getCart(@PathVariable Long userId) {
        List<CartDto> cartItems = cartFacade.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }
}