package io.hhplus.ecommerce.application.data;

import io.hhplus.ecommerce.application.dto.order.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class DataPlatform {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(DataPlatform.class); // 로깅을 위한 Logger

    public DataPlatform(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOrderData(OrderDto orderDto) {
        String dataPlatformUrl = "https://dataplatform.example.com/api/orders";  // 데이터 플랫폼 URL

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderDto> request = new HttpEntity<>(orderDto, headers);

        try {
            // POST 요청 보내기
            restTemplate.postForEntity(dataPlatformUrl, request, String.class);
            logger.info("Order data successfully sent to the data platform.");
        } catch (RestClientException e) {
            // 요청 실패 시 예외 처리 (간단하게 로깅)
            logger.error("Failed to send order data to the data platform.", e);
        }
    }
}
