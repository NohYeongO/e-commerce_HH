package io.hhplus.ecommerce.application.data;

import io.hhplus.ecommerce.application.dto.order.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class DataPlatform {

    private final RestTemplate restTemplate;
    private final Logger log = LoggerFactory.getLogger(DataPlatform.class);

    public DataPlatform(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendOrderData(OrderDto orderDto) {
        String dataPlatformUrl = "http://localhost:4000/api/orders";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderDto> request = new HttpEntity<>(orderDto, headers);
        try {
            // POST 요청 보내기
            restTemplate.postForEntity(dataPlatformUrl, request, String.class);
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
    }
}
