package com.example.review_service.dto;

public record ProductReviewSummary(Long productId, Double averageRating, Long reviewCount) {}