package io.hhplus.ecommerce.infra.order;

import io.hhplus.ecommerce.domain.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
