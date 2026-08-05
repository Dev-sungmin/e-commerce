package com.example.order_service.service;

import com.example.order_service.client.DeductResult;
import com.example.order_service.client.InventoryClient;
import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderItem;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderItemRequest;
import com.example.order_service.exception.InsufficientStockException;
import com.example.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();

        // 1단계: Saga - 다중 상품 순차 재고 차감
        List<DeductedItem> deducted = new ArrayList<>();
        try {
            for (OrderItemRequest itemRequest : request.items()) {
                DeductResult result = inventoryClient.deduct(
                        orderId, itemRequest.productId(), itemRequest.quantity()
                );
                // 성공한 것만 기록해둠 - 나중에 실패 시 이 목록을 역순으로 되돌림
                deducted.add(new DeductedItem(itemRequest.productId(), itemRequest.quantity(), itemRequest.productName(), itemRequest.price()));
            }
        } catch (InsufficientStockException e) {
            // 보상 트랜잭션: 이미 차감된 것들을 역순으로 복구
            compensate(orderId, deducted);
            throw e; // 컨트롤러에서 409로 응답
        }

        // 2단계: 재고 차감 전부 성공 -> 주문 생성 (PENDING)
        int totalAmount = deducted.stream()
                .mapToInt(d -> d.price() * d.quantity())
                .sum();

        Order order = new Order(orderId, userId, totalAmount);
        for (DeductedItem d : deducted) {
            order.addItem(new OrderItem(d.productId(), d.productName(), d.quantity(), d.price()));
        }

        orderRepository.save(order);

        // TODO: Payment Service 연동 (결제 요청)
        // Payment Service가 아직 없어서, 지금은 PENDING 상태로 저장만 하고 끝냄.
        // 나중에 Payment 연동 시: 결제 요청 실패 -> compensate(orderId, deducted) 호출 + CANCELLED 처리 추가 예정

        return order;
    }

    // 보상 트랜잭션: 이미 차감된 항목들을 "역순으로" 복구
    private void compensate(String orderId, List<DeductedItem> deducted) {
        List<DeductedItem> reversed = new ArrayList<>(deducted);
        Collections.reverse(reversed);

        for (DeductedItem d : reversed) {
            inventoryClient.restore(orderId, d.productId(), d.quantity());
        }
    }

    private record DeductedItem(Long productId, Integer quantity, String productName, Integer price) {
    }
}