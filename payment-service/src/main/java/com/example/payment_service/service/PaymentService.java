package com.example.payment_service.service;

import com.example.payment_service.client.OrderClient;
import com.example.payment_service.client.TossClient;
import com.example.payment_service.domain.Payment;
import com.example.payment_service.domain.PaymentStatus;
import com.example.payment_service.dto.PaymentResponse;
import com.example.payment_service.exception.AmountMismatchException;
import com.example.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossClient tossClient;
    private final OrderClient orderClient;

    public PaymentService(PaymentRepository paymentRepository, TossClient tossClient, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.tossClient = tossClient;
        this.orderClient = orderClient;
    }

    @Transactional
    public PaymentResponse confirmPayment(String paymentKey, String orderId, Integer amount) {
        Optional<Payment> existing = paymentRepository.findByPaymentKey(paymentKey);

        // Idempotent Receiver: 이미 완료된 요청이면 토스 API 재호출 없이 기존 결과 반환
        if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.COMPLETED) {
            return PaymentResponse.from(existing.get());
        }

        Payment payment = existing.orElseGet(() ->
                paymentRepository.save(new Payment(paymentKey, orderId, amount))
        );

        // 클라이언트가 보낸 금액과 실제 저장된 금액 일치 검증 (금액 조작 방지)
        if (!payment.getAmount().equals(amount)) {
            throw new AmountMismatchException("요청 금액이 일치하지 않습니다");
        }

        Map<String, Object> tossResponse = tossClient.confirm(paymentKey, orderId, amount);

        payment.markCompleted();
        paymentRepository.save(payment);

        orderClient.notifyPaymentCompleted(orderId);

        return PaymentResponse.from(payment);
    }
}