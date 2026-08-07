package com.example.payment_service.dto;

public record ConfirmPaymentRequest(String paymentKey, String orderId, Integer amount) {}
