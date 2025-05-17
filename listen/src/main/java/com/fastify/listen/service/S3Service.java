package com.fastify.listen.service;

import com.fastify.listen.model.dto.ListenResponse;
import com.fastify.listen.model.dto.user.UserClaims;
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
    private final String audioSuffix;
    private final Integer urlExpirySeconds;

    public S3Service(
            @Value("${aws.region}") String awsRegion,
            @Value("${aws.bucket.name}") String bucket,
            @Value("${aws.objects.name.separator}")String objectNameSeparator,
            @Value("${aws.objects.suffix.audio}") String audioSuffix,
            @Value("${aws.url.expiry.seconds}") Integer urlExpirySeconds
    ) {
        this.awsRegion = awsRegion;
        this.bucket = bucket;
        this.objectNameSeparator = objectNameSeparator;
        this.audioSuffix = audioSuffix;
        this.urlExpirySeconds = urlExpirySeconds;
    }

    public ListenResponse generateSignedUrl(UserClaims userClaims, String videoId) {
        String bucketBaseKey = userClaims.userId() + objectNameSeparator + videoId + objectNameSeparator;
        String audioFileBucketKey = bucketBaseKey + audioSuffix;
        PresignedGetObjectRequest presignedAudioFileRequest;

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()
        ) {
            GetObjectRequest audioFileRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(audioFileBucketKey)
                    .build();

            GetObjectPresignRequest audioFilePresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(urlExpirySeconds))
                    .getObjectRequest(audioFileRequest)
                    .build();

            presignedAudioFileRequest = presigner.presignGetObject(audioFilePresignRequest);
        }

        String downloadUrl = presignedAudioFileRequest.url().toExternalForm();
        return new ListenResponse(videoId, downloadUrl);
    }
}
