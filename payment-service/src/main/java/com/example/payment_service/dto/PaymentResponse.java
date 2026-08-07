package com.example.payment_service.dto;

import com.example.payment_service.domain.Payment;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        Integer amount,
        String status
){
    public static PaymentResponse from(Payment payment){
        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus().name()
        );
    }
}
