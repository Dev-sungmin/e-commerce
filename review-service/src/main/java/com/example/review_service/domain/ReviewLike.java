package com.example.review_service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "review_likes")
public class ReviewLike {

    @Id
    private String id;

    private String reviewId;
    private Long userId;
    private LocalDateTime createdAt;

    protected ReviewLike() {
    }

    public ReviewLike(String reviewId, Long userId) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getReviewId() { return reviewId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}