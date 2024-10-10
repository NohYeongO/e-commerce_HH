package io.hhplus.swagger.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문내역")
public class OrderMockDto {

    private Long orderId;
    private Long userId;
    private List<ProductMockDto> products;
    private LocalDateTime orderDate;
    private int quantity;
}
