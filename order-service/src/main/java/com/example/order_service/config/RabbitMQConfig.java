package com.example.order_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String WAIT_QUEUE = "order.timeout.wait";
    public static final String PROCESS_QUEUE = "order.timeout.process";
    public static final String DLX_EXCHANGE = "order.timeout.dlx";

    @Bean
    public Queue waitQueue() {
        return QueueBuilder.durable(WAIT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PROCESS_QUEUE)
                .withArgument("x-message-ttl", 600000)
                .build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue processQueue() {
        return QueueBuilder.durable(PROCESS_QUEUE).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(processQueue()).to(dlxExchange()).with(PROCESS_QUEUE);
    }
}