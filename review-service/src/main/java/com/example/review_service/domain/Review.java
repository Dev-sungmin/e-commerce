package com.example.review_service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    private String orderId;
    private Long productId;
    private Long userId;
    private String userEmail;
    private Integer rating;
    private String content;
    private List<String> imageUrls = new ArrayList<>();
    private Integer likeCount = 0;
    private LocalDateTime createdAt;

    protected Review() {
    }

    public Review(String orderId, Long productId, Long userId, String userEmail,
                  Integer rating, String content, List<String> imageUrls) {
        this.orderId = orderId;
        this.productId = productId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.rating = rating;
        this.content = content;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public Long getProductId() { return productId; }
    public Long getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public Integer getRating() { return rating; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return imageUrls; }
    public Integer getLikeCount() { return likeCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}