package io.hhplus.ecommerce.domain.service.product;

import io.hhplus.ecommerce.infra.product.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockDeductionServiceTest {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @BeforeEach
    void setUp() {

    }



}