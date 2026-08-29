package com.example.review_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewListResponse(
        List<ReviewResponse> reviews,
        LocalDateTime nextCursor,
        boolean hasNext
) {
    public static ReviewListResponse of(List<ReviewResponse> reviews, int requestedSize) {
        boolean hasNext = reviews.size() > requestedSize;
        List<ReviewResponse> trimmed = hasNext ? reviews.subList(0, requestedSize) : reviews;
        LocalDateTime nextCursor = trimmed.isEmpty() ? null : trimmed.get(trimmed.size() - 1).createdAt();
        return new ReviewListResponse(trimmed, nextCursor, hasNext);
    }
}