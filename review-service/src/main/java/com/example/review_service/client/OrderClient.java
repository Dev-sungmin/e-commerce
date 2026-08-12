package com.example.review_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OrderClient {

    private final RestClient restClient;

    public OrderClient(RestClient.Builder builder, @Value("${order.base-url}") String orderBaseUrl) {
        this.restClient = builder.baseUrl(orderBaseUrl).build();
    }

    public boolean checkPurchase(String orderId, Long userId, Long productId) {
        Map<String, Boolean> response = restClient.get()
                .uri("/internal/orders/{orderId}/purchase-check?userId={userId}&productId={productId}",
                        orderId, userId, productId)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Boolean>>() {});

        if (response == null) {
            return false;
        }
        return Boolean.TRUE.equals(response.get("purchased"));
    }
}