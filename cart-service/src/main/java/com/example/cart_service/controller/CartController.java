package com.example.cart_service.controller;

import com.example.cart_service.dto.CartItemRequest;
import com.example.cart_service.dto.CartItemUpdateRequest;
import com.example.cart_service.dto.CartResponse;
import com.example.cart_service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CartItemRequest request
    ) {
        cartService.addItem(userId, request.productId(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<Void> updateQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId,
            @RequestBody CartItemUpdateRequest request
    ) {
        cartService.updateQuantity(userId, productId, request.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId
    ) {
        cartService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}