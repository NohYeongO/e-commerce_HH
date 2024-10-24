package io.hhplus.ecommerce.domain.service.cart;

import io.hhplus.ecommerce.application.dto.cart.CartDto;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import io.hhplus.ecommerce.common.exception.CartOperationException;
import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.domain.entity.cart.Cart;
import io.hhplus.ecommerce.infra.cart.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateCartService {
    private final Logger log = LoggerFactory.getLogger(UpdateCartService.class);
    private final CartJpaRepository cartJpaRepository;

    // 장바구니에 상품 추가
    public void addCart(CartDto cartDto) {
        try{
            for (ProductDto productDto : cartDto.getProducts()) {
                if (productDto != null) {
                    Cart cart = Cart.builder()
                            .user(cartDto.getUser().toEntity())
                            .product(productDto.toEntity())
                            .quantity(productDto.getQuantity())
                            .build();
                    cartJpaRepository.save(cart);
                } else {
                    log.warn("ProductDto is null, skipping this item.");
                }
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Add Cart DB Error: {}", e.getMessage());
            throw new CartOperationException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
    }

    // 장바구니에서 상품 삭제
    public void removeCart(Long cartId) {
        try{
            if(cartId != null){
                cartJpaRepository.deleteById(cartId);
            }else{
                log.error("Remove Cart Error, cartId is null");
                throw new CartOperationException(ErrorCode.INVALID_REQUEST);
            }
        }catch (DataIntegrityViolationException e) {
            log.error("Remove Cart DB Error: {}", e.getMessage());
            throw new CartOperationException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
    }
}
