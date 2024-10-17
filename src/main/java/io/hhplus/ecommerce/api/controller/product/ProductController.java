package io.hhplus.ecommerce.api.controller.product;

import io.hhplus.ecommerce.application.facade.QueryFacade;
import io.hhplus.ecommerce.application.dto.product.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/product")
@RequiredArgsConstructor
public class ProductController {

    private final QueryFacade queryFacade;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("id") Long productId) {
        return ResponseEntity.ok(queryFacade.getProduct(productId));
    }

    @GetMapping("/top5")
    public ResponseEntity<List<ProductDto>> getTop5Products() {
        return ResponseEntity.ok(queryFacade.getTopFiveProducts());
    }

}
