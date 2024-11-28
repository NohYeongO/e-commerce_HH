package io.hhplus.ecommerce.infra.product;

import io.hhplus.ecommerce.domain.entity.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
    List<Product> findAllById(List<Long> productIds);

    // 상품 비관적락 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByIdWithLock(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderDetail od ON p.productId = od.product.productId " +
            "JOIN Order o ON od.order.orderId = o.orderId " +
            "WHERE o.orderDate BETWEEN :startDate AND CURRENT_DATE " +
            "GROUP BY p.productId " +
            "ORDER BY SUM(od.quantity) DESC")
    Page<Product> findTop5Product(LocalDateTime startDate, Pageable pageable);
}
