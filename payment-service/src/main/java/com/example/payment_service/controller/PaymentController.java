package com.example.payment_service.controller;

import com.example.payment_service.dto.ConfirmPaymentRequest;
import com.example.payment_service.dto.PaymentResponse;
import com.example.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestBody ConfirmPaymentRequest request) {
        PaymentResponse response = paymentService.confirmPayment(
                request.paymentKey(), request.orderId(), request.amount()
        );
        return ResponseEntity.ok(response);
    }
}