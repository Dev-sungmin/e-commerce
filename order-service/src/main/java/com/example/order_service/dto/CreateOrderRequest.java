package com.example.order_service.dto;

import java.util.List;

public record CreateOrderRequest(List<OrderItemRequest> items) {
}