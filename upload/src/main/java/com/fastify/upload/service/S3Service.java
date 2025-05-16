package com.fastify.upload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucket;

    public S3Service(
            S3Client s3Client,
            @Value("${aws.bucket.name}") String bucket
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
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
}
