package io.hhplus.ecommerce.domain.entity.user;

import io.hhplus.ecommerce.common.exception.product.ProductNotFoundException;
import io.hhplus.ecommerce.domain.entity.cart.Cart;
import io.hhplus.ecommerce.domain.entity.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "POINT")
    private BigDecimal point;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @Builder
    public User(Long userId, String name, BigDecimal point) {
        this.userId = userId;
        this.name = name;
        this.point = point;
    }

    public void deduction(BigDecimal deductionPoint) {
        BigDecimal newPoint = this.point.subtract(deductionPoint);

        if (newPoint.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point = newPoint;
    }

    public void addCart(Cart cart) {
        this.carts.add(cart);
        Cart.builder().user(this);
    }

    public void addPoint(BigDecimal point) {
        this.point = this.point.add(point);
    }

}
