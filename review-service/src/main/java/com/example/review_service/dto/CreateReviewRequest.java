package com.example.review_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateReviewRequest(
        @NotNull Long productId,
        @NotBlank String orderId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotBlank String content,
        List<String> imageUrls
) {}