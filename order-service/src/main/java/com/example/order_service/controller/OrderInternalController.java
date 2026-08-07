package com.example.order_service.controller;

import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderStatus;
import com.example.order_service.dto.UpdateOrderStatusRequest;
import com.example.order_service.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/internal/orders")
public class OrderInternalController {

    private final OrderRepository orderRepository;

    public OrderInternalController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        Optional<Order> orderOpt = orderRepository.findById(id);

        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ORDER_NOT_FOUND"));
        }

        Order order = orderOpt.get();

        if (order.getStatus() != OrderStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "INVALID_ORDER_STATE", "currentStatus", order.getStatus().name()));
        }

        switch (request.status()) {
            case PAID -> order.markPaid();
            case CANCELLED -> order.markCancelled();
            default -> {
                return ResponseEntity.badRequest().body(Map.of("error", "UNSUPPORTED_STATUS"));
            }
        }

        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }
}