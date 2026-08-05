package com.example.order_service.client;

import com.example.order_service.exception.InsufficientStockException;
import com.example.order_service.exception.InventoryServiceUnavailableException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(RestClient.Builder builder, @Value("${php.base-url}") String phpBaseUrl) {
        this.restClient = builder.baseUrl(phpBaseUrl).build();
    }

    @CircuitBreaker(name = "phpInventoryService", fallbackMethod = "deductFallback")
    @Bulkhead(name = "phpInventoryService")
    public DeductResult deduct(String orderId, Long productId, Integer quantity) {
        try {
            return restClient.post()
                    .uri("/internal/inventory.php?action=deduct")
                    .body(new InventoryRequest(orderId, productId, quantity))
                    .retrieve()
                    .body(DeductResult.class);
        } catch (HttpClientErrorException.Conflict e) {
            throw new InsufficientStockException(productId, "재고가 부족합니다: productId=" + productId);
        }
    }

    @CircuitBreaker(name = "phpInventoryService", fallbackMethod = "restoreFallback")
    @Bulkhead(name = "phpInventoryService")
    public void restore(String orderId, Long productId, Integer quantity) {
        restClient.post()
                .uri("/internal/inventory.php?action=restore")
                .body(new InventoryRequest(orderId, productId, quantity))
                .retrieve()
                .toBodilessEntity();
    }

    // fallback이 실행되더라도, "진짜 인프라 장애"가 아니라 "정상적인 비즈니스 거절"이면
    // 그대로 원래 예외를 다시 던져서 GlobalExceptionHandler가 409로 처리하게 함
    private DeductResult deductFallback(String orderId, Long productId, Integer quantity, Throwable t) {
        if (t instanceof InsufficientStockException) {
            throw (InsufficientStockException) t;
        }
        throw new InventoryServiceUnavailableException("재고 서비스에 연결할 수 없습니다: " + t.getMessage());
    }

    private void restoreFallback(String orderId, Long productId, Integer quantity, Throwable t) {
        throw new InventoryServiceUnavailableException("재고 서비스에 연결할 수 없습니다: " + t.getMessage());
    }
}