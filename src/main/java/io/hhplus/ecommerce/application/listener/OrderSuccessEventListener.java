package io.hhplus.ecommerce.application.listener;

import io.hhplus.ecommerce.application.data.DataPlatform;
import io.hhplus.ecommerce.application.event.OrderSuccessEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderSuccessEventListener {

    private final DataPlatform dataPlatform;

    public OrderSuccessEventListener(DataPlatform dataPlatform) {
        this.dataPlatform = dataPlatform;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderSuccessEvent event) {
        dataPlatform.sendOrderData(event.getOrder());
    }
}
