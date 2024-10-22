package io.hhplus.ecommerce.infra.cart;

import io.hhplus.ecommerce.domain.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartJpaRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_UserId(Long userId);

}
