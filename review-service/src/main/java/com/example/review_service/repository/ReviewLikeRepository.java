package com.example.review_service.repository;

import com.example.review_service.domain.ReviewLike;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewLikeRepository extends MongoRepository<ReviewLike, String> {
    boolean existsByReviewIdAndUserId(String reviewId, Long userId);
    void deleteByReviewIdAndUserId(String reviewId, Long userId);
}