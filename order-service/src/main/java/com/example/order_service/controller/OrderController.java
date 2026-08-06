package com.example.order_service.controller;

import com.example.order_service.domain.Order;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request
    ) {
        Order order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrder(@PathVariable String id) {
        Optional<Order> order = orderRepository.findWithItemsById(id);

        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ORDER_NOT_FOUND"));
        }

        return ResponseEntity.ok(OrderResponse.from(order.get()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@RequestHeader("X-User-Id") Long userId) {
        List<Order> orders = orderRepository.findWithItemsByUserIdOrderByCreatedAtDesc(userId);
        List<OrderResponse> response = orders.stream().map(OrderResponse::from).toList();
        return ResponseEntity.ok(response);
    }
}