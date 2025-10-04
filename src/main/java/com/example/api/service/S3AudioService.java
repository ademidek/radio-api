package com.example.api.service;

import java.net.URL;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;

@Service
public class S3AudioService {

    private final String bucket;
    private final S3Presigner presigner;

    public S3AudioService(@Value("${aws.s3.bucket}") String bucket,
                          @Value("${aws.region}") Region region) {
        this.bucket = bucket;
        this.presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        }

        public URL generatePresignedUrl(String s3Key) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url();
        }
    
}
