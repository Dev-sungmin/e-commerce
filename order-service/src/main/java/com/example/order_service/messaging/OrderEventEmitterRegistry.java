package com.example.order_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderEventEmitterRegistry {

    private static final Logger log = LoggerFactory.getLogger(OrderEventEmitterRegistry.class);

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String orderId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(orderId, emitter);

        emitter.onCompletion(() -> emitters.remove(orderId));
        emitter.onTimeout(() -> emitters.remove(orderId));
        emitter.onError((e) -> emitters.remove(orderId));

        return emitter;
    }

    public void notifyCancelled(String orderId) {
        SseEmitter emitter = emitters.get(orderId);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event().name("cancelled").data(orderId));
            emitter.complete();
        } catch (IOException e) {
            log.warn("SSE 알림 전송 실패: orderId={}", orderId);
            emitters.remove(orderId);
        }
    }
}