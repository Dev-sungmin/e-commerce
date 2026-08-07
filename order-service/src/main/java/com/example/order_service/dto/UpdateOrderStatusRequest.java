package com.example.order_service.dto;

import com.example.order_service.domain.OrderStatus;

public record UpdateOrderStatusRequest(OrderStatus status) {}