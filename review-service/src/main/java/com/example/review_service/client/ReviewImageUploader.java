package com.example.review_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Component
public class ReviewImageUploader {

    private final S3Presigner presigner;
    private final String bucketName;
    private final String region;

    public ReviewImageUploader(
            S3Presigner presigner,
            @Value("${aws.s3.review-bucket}") String bucketName,
            @Value("${aws.s3.region}") String region
    ) {
        this.presigner = presigner;
        this.bucketName = bucketName;
        this.region = region;
    }

    public PresignedUploadResult createPresignedUploadUrl(String originalFilename) {
        String key = "reviews/" + UUID.randomUUID() + "-" + originalFilename;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();

        String uploadUrl = presigner.presignPutObject(presignRequest).url().toString();
        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

        return new PresignedUploadResult(uploadUrl, publicUrl);
    }

    public record PresignedUploadResult(String uploadUrl, String publicUrl) {}
}