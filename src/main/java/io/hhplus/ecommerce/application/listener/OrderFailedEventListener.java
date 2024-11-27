package io.hhplus.ecommerce.application.listener;

import io.hhplus.ecommerce.application.event.OrderFailedEvent;
import io.hhplus.ecommerce.domain.service.product.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderFailedEventListener {

    private final ProductStockService productStockService;
    private static final Logger log = LoggerFactory.getLogger(OrderFailedEventListener.class);


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void handleOrderFailed(OrderFailedEvent event) {
        try{
            // 1. 보상 트랜잭션: 재고 복구
            productStockService.addStock(event.getOrderDto().getOrderDetails());
            // 2. 실패 로그 기록
            log.error("Order failed for userId={}, reason={}", event.getOrderDto().getUserId(), event.getReason());
        } catch (Exception e){
            // 보상 트랜잭션 오류시 확인을 위한 로그 기록
            log.error("addStock failed: order={}", event.getOrderDto());
        }
    }
}
