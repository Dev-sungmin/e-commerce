package com.example.payment_service.client;

import com.example.payment_service.dto.UpdateOrderStatusRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String orderBaseUrl;

    public OrderClient(RestTemplate restTemplate, @Value("${order.base-url}") String orderBaseUrl) {
        this.restTemplate = restTemplate;
        this.orderBaseUrl = orderBaseUrl;
    }

    public void notifyPaymentCompleted(String orderId) {
        try {
            restTemplate.exchange(
                    orderBaseUrl + "/internal/orders/" + orderId + "/status",
                    HttpMethod.PATCH,
                    new HttpEntity<>(new UpdateOrderStatusRequest("PAID")),
                    Void.class
            );
        } catch (RestClientException e) {
        }
    }
}