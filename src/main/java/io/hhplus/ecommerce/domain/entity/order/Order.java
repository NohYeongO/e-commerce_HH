package io.hhplus.ecommerce.domain.entity.order;

import io.hhplus.ecommerce.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Builder
    public Order(User user, LocalDateTime orderDate, List<OrderDetail> orderDetails) {
        this.user = user;
        this.orderDate = orderDate;
        if(orderDetails != null) {
            List<OrderDetail> newOrderDetails = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetails) {
                OrderDetail newOrderDetail = OrderDetail.builder()
                        .order(this)
                        .product(orderDetail.getProduct())
                        .quantity(orderDetail.getQuantity())
                        .build();
                newOrderDetails.add(newOrderDetail);
            }
            this.orderDetails.addAll(newOrderDetails);
        }
    }
}


