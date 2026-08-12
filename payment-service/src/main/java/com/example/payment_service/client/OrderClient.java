package com.example.payment_service.client;

import com.example.payment_service.dto.UpdateOrderStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OrderClient {

    private static final Logger log = LoggerFactory.getLogger(OrderClient.class);

    private final RestClient restClient;

    public OrderClient(RestClient.Builder builder, @Value("${order.base-url}") String orderBaseUrl) {
        this.restClient = builder.baseUrl(orderBaseUrl).build();
    }

    public void notifyPaymentCompleted(String orderId) {
        try {
            restClient.patch()
                    .uri("/internal/orders/{orderId}/status", orderId)
                    .body(new UpdateOrderStatusRequest("PAID"))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Order 상태 갱신 성공: orderId={}", orderId);
        } catch (RestClientException e) {
            log.error("Order 상태 갱신 실패: orderId={}, 원인={}", orderId, e.getMessage());
        }
    }
}