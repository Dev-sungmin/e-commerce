package com.example.review_service.controller;

import com.example.review_service.client.ReviewImageUploader;
import com.example.review_service.dto.*;
import com.example.review_service.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewImageUploader reviewImageUploader;

    public ReviewController(ReviewService reviewService, ReviewImageUploader reviewImageUploader) {
        this.reviewService = reviewService;
        this.reviewImageUploader = reviewImageUploader;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestBody CreateReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(userId, userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ReviewListResponse> getReviews(
            @RequestParam Long productId,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        ReviewListResponse reviews = reviewService.getReviewsByProduct(productId, cursor, size, userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeReview(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        reviewService.likeReview(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeReview(
            @PathVariable String id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        reviewService.unlikeReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-url")
    public ResponseEntity<UploadUrlResponse> getUploadUrl(@RequestParam String filename) {
        var result = reviewImageUploader.createPresignedUploadUrl(filename);
        return ResponseEntity.ok(new UploadUrlResponse(result.uploadUrl(), result.publicUrl()));
    }

    @GetMapping("/summary")
    public ResponseEntity<ReviewSummary> getSummary(@RequestParam Long productId) {
        return ResponseEntity.ok(reviewService.getReviewSummary(productId));
    }

    @GetMapping("/summary/batch")
    public ResponseEntity<List<ProductReviewSummary>> getSummaries(@RequestParam List<Long> productIds) {
        return ResponseEntity.ok(reviewService.getReviewSummaries(productIds));
    }

    @GetMapping("/by-order")
    public ResponseEntity<ReviewResponse> getReviewByOrder(
            @RequestParam String orderId,
            @RequestParam Long productId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        ReviewResponse response = reviewService.getReviewByOrder(orderId, productId, userId);
        return ResponseEntity.ok(response);
    }
}