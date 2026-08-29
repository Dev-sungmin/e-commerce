package com.example.review_service.repository;

import com.example.review_service.domain.Review;
import com.example.review_service.dto.ProductReviewSummary;
import com.example.review_service.dto.ReviewSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    List<Review> findByProductIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            Long productId, LocalDateTime cursor, Pageable pageable);

    Optional<Review> findByOrderIdAndProductId(String orderId, Long productId);
    boolean existsByOrderIdAndProductId(String orderId, Long productId);

    @Aggregation(pipeline = {
            "{ '$match': { 'productId': ?0 } }",
            "{ '$group': { '_id': '$productId', 'averageRating': { '$avg': '$rating' }, 'reviewCount': { '$sum': 1 } } }"
    })
    Optional<ReviewSummary> getReviewSummary(Long productId);

    @Aggregation(pipeline = {
            "{ '$match': { 'productId': { '$in': ?0 } } }",
            "{ '$group': { '_id': '$productId', 'averageRating': { '$avg': '$rating' }, 'reviewCount': { '$sum': 1 } } }",
            "{ '$project': { 'productId': '$_id', 'averageRating': 1, 'reviewCount': 1, '_id': 0 } }"
    })
    List<ProductReviewSummary> getReviewSummaries(List<Long> productIds);
}