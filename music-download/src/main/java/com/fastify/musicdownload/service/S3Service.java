package com.fastify.musicdownload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    public void putObject(String bucket, String key, Path filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, filePath);
    }

    public byte[] getObject(String bucket, String key) {
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
