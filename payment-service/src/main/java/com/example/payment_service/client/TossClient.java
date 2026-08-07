package com.example.payment_service.client;

import com.example.payment_service.exception.TossApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Component
public class TossClient {

    private final RestTemplate restTemplate;
    private final String secretKey;
    private final String confirmUrl;

    public TossClient(
            RestTemplate restTemplate,
            @Value("${toss.secret-key}") String secretKey,
            @Value("${toss.confirm-url}") String confirmUrl
    ) {
        this.restTemplate = restTemplate;
        this.secretKey = secretKey;
        this.confirmUrl = confirmUrl;
    }

    public Map<String, Object> confirm(String paymentKey, String orderId, Integer amount) {
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    confirmUrl,
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new TossApiException("토스 결제 승인 실패: " + e.getMessage());
        }
    }
}