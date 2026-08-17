package com.example.order_service.messaging;

import com.example.order_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderTimeoutPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderTimeoutPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void scheduleTimeout(String orderId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.WAIT_QUEUE, orderId);
    }
}