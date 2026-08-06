package com.example.cart_service.service;

import com.example.cart_service.dto.CartResponse;
import com.example.cart_service.exception.InvalidQuantityException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private static final Duration CART_TTL = Duration.ofDays(7);

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOps;

    public CartService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    // Redis HINCRBY로 원자적 수량 증가 (read-modify-write 시 동시 요청에서 lost update 가능)
    public void addItem(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("quantity must be positive");
        }
        String cartKey = cartKey(userId);
        hashOps.increment(cartKey, productId.toString(), quantity);
        redisTemplate.expire(cartKey, CART_TTL);
    }

    public CartResponse getCart(Long userId) {
        Map<String, String> entries = hashOps.entries(cartKey(userId));

        List<CartResponse.CartItemResponse> items = entries.entrySet().stream()
                .map(e -> new CartResponse.CartItemResponse(
                        Long.parseLong(e.getKey()),
                        Integer.parseInt(e.getValue())))
                .toList();

        int totalQuantity = items.stream()
                .mapToInt(CartResponse.CartItemResponse::quantity)
                .sum();

        return new CartResponse(items, totalQuantity);
    }

    // 절대값 지정 (증가가 아니라 덮어쓰기)
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("quantity must be positive");
        }
        String cartKey = cartKey(userId);
        hashOps.put(cartKey, productId.toString(), quantity.toString());
        redisTemplate.expire(cartKey, CART_TTL);
    }

    public void removeItem(Long userId, Long productId) {
        hashOps.delete(cartKey(userId), productId.toString());
    }

    public void clearCart(Long userId) {
        redisTemplate.delete(cartKey(userId));
    }

    private String cartKey(Long userId) {
        return "cart:" + userId;
    }
}