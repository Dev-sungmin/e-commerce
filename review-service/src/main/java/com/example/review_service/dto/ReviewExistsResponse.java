package com.example.review_service.dto;

public record ReviewExistsResponse(boolean exists, ReviewResponse review) {
}