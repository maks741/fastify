package com.fastify.user.service;

import com.fastify.user.model.dto.user.UserClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
public class S3Service {

    private final String awsRegion;
    private final String bucket;
    private final String objectNameSeparator;
    private final String thumbnailSuffix;
    private final Integer urlExpirySeconds;

    public S3Service(
            @Value("${aws.region}") String awsRegion,
            @Value("${aws.bucket.name}") String bucket,
            @Value("${aws.objects.name.separator}")String objectNameSeparator,
            @Value("${aws.objects.suffix.thumbnail}") String thumbnailSuffix,
            @Value("${aws.url.expiry.seconds}") Integer urlExpirySeconds
    ) {
        this.awsRegion = awsRegion;
        this.bucket = bucket;
        this.objectNameSeparator = objectNameSeparator;
        this.urlExpirySeconds = urlExpirySeconds;
        this.thumbnailSuffix = thumbnailSuffix;
    }

    public String generateSignedThumbnailUrl(UserClaims userClaims, String videoId) {
        String bucketBaseKey = userClaims.userId() + objectNameSeparator + videoId + objectNameSeparator;
        String thumbnailFileBucketKey = bucketBaseKey + thumbnailSuffix;
        PresignedGetObjectRequest presignedThumbnailFileRequest;

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {
            GetObjectRequest thumbnailFileRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(thumbnailFileBucketKey)
                    .build();

            GetObjectPresignRequest thumbnailFilePresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(urlExpirySeconds))
                    .getObjectRequest(thumbnailFileRequest)
                    .build();

            presignedThumbnailFileRequest = presigner.presignGetObject(thumbnailFilePresignRequest);
        }

        return presignedThumbnailFileRequest.url().toExternalForm();
    }
}
