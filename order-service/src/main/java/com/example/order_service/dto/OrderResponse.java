package com.example.order_service.dto;

import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        Long userId,
        Integer totalAmount,
        String status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt(),
                items
        );
    }

    public record OrderItemResponse(Long productId, String productName, Integer quantity, Integer price) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProductId(), item.getProductName(), item.getQuantity(), item.getPrice()
            );
        }
    }
}