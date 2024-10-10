package io.hhplus.swagger.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품정보")
public class ProductMockDto {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private int stock;
}
