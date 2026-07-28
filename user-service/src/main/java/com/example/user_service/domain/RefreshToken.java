package com.example.user_service.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(unique = true, nullable = false, length = 512)
    private String token;

    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    protected RefreshToken() {
    }

    public RefreshToken(Long userId, String token, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }

    public Long getUserId() { return userId; }
    public String getToken() { return token; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}