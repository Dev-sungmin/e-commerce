package com.example.order_service.service;

import com.example.order_service.client.DeductResult;
import com.example.order_service.client.InventoryClient;
import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderItem;
import com.example.order_service.domain.OrderStatus;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderItemRequest;
import com.example.order_service.exception.InsufficientStockException;
import com.example.order_service.exception.InvalidOrderStateException;
import com.example.order_service.exception.OrderAccessDeniedException;
import com.example.order_service.exception.OrderNotFoundException;
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

        List<DeductedItem> deducted = new ArrayList<>();
        try {
            for (OrderItemRequest itemRequest : request.items()) {
                DeductResult result = inventoryClient.deduct(
                        orderId, itemRequest.productId(), itemRequest.quantity()
                );
                deducted.add(new DeductedItem(itemRequest.productId(), itemRequest.quantity(), result.productName(), result.price()));
            }
        } catch (InsufficientStockException e) {
            compensate(orderId, deducted);
            throw e;
        }

        int totalAmount = deducted.stream()
                .mapToInt(d -> d.price() * d.quantity())
                .sum();

        Order order = new Order(orderId, userId, totalAmount);
        for (DeductedItem d : deducted) {
            order.addItem(new OrderItem(d.productId(), d.productName(), d.quantity(), d.price()));
        }

        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void cancelOrder(String orderId, Long userId) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUserId().equals(userId)) {
            throw new OrderAccessDeniedException("본인의 주문만 취소할 수 있습니다");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("결제 대기 상태의 주문만 취소할 수 있습니다");
        }

        for (OrderItem item : order.getItems()) {
            inventoryClient.restore(orderId, item.getProductId(), item.getQuantity());
        }

        order.markCancelled();
        orderRepository.save(order);
    }

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