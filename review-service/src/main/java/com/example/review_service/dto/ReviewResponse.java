package com.example.review_service.dto;

import com.example.review_service.domain.Review;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        String id,
        Long productId,
        String userEmail,
        Integer rating,
        String content,
        List<String> imageUrls,
        Integer likeCount,
        boolean likedByMe,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review, boolean likedByMe) {
        return new ReviewResponse(
                review.getId(),
                review.getProductId(),
                review.getUserEmail(),
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getLikeCount(),
                likedByMe,
                review.getCreatedAt()
        );
    }
}