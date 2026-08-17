package com.example.order_service.messaging;

import com.example.order_service.client.InventoryClient;
import com.example.order_service.config.RabbitMQConfig;
import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderItem;
import com.example.order_service.domain.OrderStatus;
import com.example.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderTimeoutConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutConsumer.class);

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderEventEmitterRegistry orderEventEmitterRegistry;

    public OrderTimeoutConsumer(OrderRepository orderRepository, InventoryClient inventoryClient,
                                OrderEventEmitterRegistry orderEventEmitterRegistry) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.orderEventEmitterRegistry = orderEventEmitterRegistry;
    }

    @RabbitListener(queues = RabbitMQConfig.PROCESS_QUEUE)
    @Transactional
    public void handleTimeout(String orderId) {
        orderRepository.findWithItemsById(orderId).ifPresentOrElse(order -> {
            if (order.getStatus() != OrderStatus.PENDING) {
                log.info("주문 {}는 이미 {} 상태 - 타임아웃 처리 스킵", orderId, order.getStatus());
                return;
            }

            for (OrderItem item : order.getItems()) {
                inventoryClient.restore(orderId, item.getProductId(), item.getQuantity());
            }
            order.markCancelled();
            orderRepository.save(order);

            orderEventEmitterRegistry.notifyCancelled(orderId);

            log.info("주문 {} 타임아웃으로 자동 취소 및 재고 복구 완료", orderId);
        }, () -> log.warn("타임아웃 처리 대상 주문을 찾을 수 없음: {}", orderId));
    }
}