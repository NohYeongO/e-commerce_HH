package io.hhplus.ecommerce.application.dto.product;

import io.hhplus.ecommerce.domain.entity.product.Product;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private String name;
    private BigDecimal price;
    private int stock;

    private int quantity;
    private List<ProductDto> productList;

    public Product toEntity(){
        return Product.builder()
                .productId(this.productId)
                .name(this.name)
                .price(this.price)
                .stock(this.stock)
                .build();
    }

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }


}
