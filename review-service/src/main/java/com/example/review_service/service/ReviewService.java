package com.example.review_service.service;

import com.example.review_service.client.OrderClient;
import com.example.review_service.domain.Review;
import com.example.review_service.dto.CreateReviewRequest;
import com.example.review_service.dto.ProductReviewSummary;
import com.example.review_service.dto.ReviewResponse;
import com.example.review_service.dto.ReviewSummary;
import com.example.review_service.exception.DuplicateReviewException;
import com.example.review_service.exception.NotPurchasedException;
import com.example.review_service.exception.ReviewNotFoundException;
import com.example.review_service.repository.ReviewLikeRepository;
import com.example.review_service.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final OrderClient orderClient;

    public ReviewService(ReviewRepository reviewRepository,
                         ReviewLikeRepository reviewLikeRepository,
                         OrderClient orderClient) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.orderClient = orderClient;
    }

    public ReviewResponse createReview(Long userId, String userEmail, CreateReviewRequest request) {
        if (reviewRepository.existsByOrderIdAndProductId(request.orderId(), request.productId())) {
            throw new DuplicateReviewException("이미 이 주문에 대한 리뷰를 작성했습니다");
        }

        boolean purchased = orderClient.checkPurchase(request.orderId(), userId, request.productId());
        if (!purchased) {
            throw new NotPurchasedException("구매하지 않은 상품에는 리뷰를 작성할 수 없습니다");
        }

        Review review = new Review(
                request.orderId(),
                request.productId(),
                userId,
                userEmail,
                request.rating(),
                request.content(),
                request.imageUrls()
        );
        Review saved = reviewRepository.save(review);

        return ReviewResponse.from(saved, false);
    }

    public Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable, Long requesterId) {
        Page<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        return reviews.map(review -> {
            boolean likedByMe = requesterId != null
                    && reviewLikeRepository.existsByReviewIdAndUserId(review.getId(), requesterId);
            return ReviewResponse.from(review, likedByMe);
        });
    }

    public void likeReview(String reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다"));

        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            return; // 이미 좋아요 누른 상태면 그냥 무시 (멱등)
        }

        reviewLikeRepository.save(new com.example.review_service.domain.ReviewLike(reviewId, userId));
        review.increaseLikeCount();
        reviewRepository.save(review);
    }

    public void unlikeReview(String reviewId, Long userId) {
        if (!reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            return; // 좋아요 안 누른 상태면 그냥 무시
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다"));

        reviewLikeRepository.deleteByReviewIdAndUserId(reviewId, userId);
        review.decreaseLikeCount();
        reviewRepository.save(review);
    }

    public ReviewSummary getReviewSummary(Long productId) {
        return reviewRepository.getReviewSummary(productId)
                .orElse(new ReviewSummary(0.0, 0L));
    }

    public List<ProductReviewSummary> getReviewSummaries(List<Long> productIds) {
        return reviewRepository.getReviewSummaries(productIds);
    }

    public ReviewResponse getReviewByOrder(String orderId, Long productId, Long requesterId) {
        Review review = reviewRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new ReviewNotFoundException("작성된 리뷰가 없습니다"));

        boolean likedByMe = requesterId != null
                && reviewLikeRepository.existsByReviewIdAndUserId(review.getId(), requesterId);
        return ReviewResponse.from(review, likedByMe);
    }

}