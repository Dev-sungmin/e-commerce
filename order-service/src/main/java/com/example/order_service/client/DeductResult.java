package com.example.order_service.client;

public record DeductResult(boolean success, Integer remainingStock, String productName, Integer price, String error) {}