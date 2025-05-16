package com.fastify.download.service;

import com.fastify.download.exception.FileDoesNotExistException;
import com.fastify.download.model.DownloadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucket;
    private final String objectNameSeparator;
    private final String audioSuffix;
    private final String thumbnailSuffix;

    public S3Service(
            S3Client s3Client,
            @Value("${aws.bucket.name}") String bucket,
            @Value("${aws.objects.name.separator}")String objectNameSeparator,
            @Value("${aws.objects.suffix.audio}") String audioSuffix,
            @Value("${aws.objects.suffix.thumbnail}") String thumbnailSuffix
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.objectNameSeparator = objectNameSeparator;
        this.audioSuffix = audioSuffix;
        this.thumbnailSuffix = thumbnailSuffix;
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

    public void putObject(String key, Path filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, filePath);
    }

    public void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public byte[] getObject(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> responseResponseInputStream = s3Client.getObject(getObjectRequest);
        byte[] bytes;
        try {
            bytes = responseResponseInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    private void verifyExists(Path path) {
        if (!Files.exists(path)) {
            throw new FileDoesNotExistException("File does not exist by that path: " + path);
        }
    }
}
