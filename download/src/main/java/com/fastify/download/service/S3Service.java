package com.fastify.download.service;

import com.fastify.download.exception.FileDoesNotExistException;
import com.fastify.download.model.DownloadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Service
public class S3Service implements FileStorage {

    private final S3Client s3Client;
    private final String awsRegion;
    private final String bucket;
    private final String objectNameSeparator;
    private final String audioSuffix;
    private final String thumbnailSuffix;
    private final Integer urlExpirySeconds;

    public S3Service(
            S3Client s3Client,
            @Value("${aws.region}") String awsRegion,
            @Value("${aws.bucket.name}") String bucket,
            @Value("${aws.objects.name.separator}")String objectNameSeparator,
            @Value("${aws.objects.suffix.audio}") String audioSuffix,
            @Value("${aws.objects.suffix.thumbnail}") String thumbnailSuffix,
            @Value("${aws.url.expiry.seconds}") Integer urlExpirySeconds
    ) {
        this.s3Client = s3Client;
        this.awsRegion = awsRegion;
        this.bucket = bucket;
        this.objectNameSeparator = objectNameSeparator;
        this.audioSuffix = audioSuffix;
        this.thumbnailSuffix = thumbnailSuffix;
        this.urlExpirySeconds = urlExpirySeconds;
    }

    public void store(Long userId, DownloadResult downloadResult) {
        String bucketBaseKey = userId + objectNameSeparator + downloadResult.videoId() + objectNameSeparator;
        String audioFileBucketKey = bucketBaseKey + audioSuffix;
        String thumbnailFileBucketKey = bucketBaseKey + thumbnailSuffix;
        Path audioPath = Paths.get(downloadResult.audioPath());
        Path thumbnailPath = Paths.get(downloadResult.thumbnailPath());

        verifyExists(audioPath);
        verifyExists(thumbnailPath);

        PutObjectRequest putAudioFileRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(audioFileBucketKey)
                .build();
        PutObjectRequest putThumbnailFileRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(thumbnailFileBucketKey)
                .build();

        s3Client.putObject(putAudioFileRequest, audioPath);
        s3Client.putObject(putThumbnailFileRequest, thumbnailPath);
    }

    public String generateThumbnailUrl(Long userId, String videoId) {
        String bucketBaseKey = userId + objectNameSeparator + videoId + objectNameSeparator;
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

    private void verifyExists(Path path) {
        if (!Files.exists(path)) {
            throw new FileDoesNotExistException("File does not exist by that path: " + path);
        }
    }
}
