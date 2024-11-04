package io.hhplus.ecommerce.domain.entity.product;

import io.hhplus.ecommerce.common.exception.ErrorCode;
import io.hhplus.ecommerce.common.exception.StockInsufficientException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PRODUCT")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "STOCK")
    private int stock;

    @Builder
    public Product(Long productId, String name, BigDecimal price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void deduction(int quantity){
        if(this.stock < quantity){
            throw new StockInsufficientException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }

    public void addStock(int quantity) {
        this.stock += quantity;
    }
}
