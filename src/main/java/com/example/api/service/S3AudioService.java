package com.example.api.service;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3AudioService {

    private final String bucket;
    private final S3Presigner presigner;
    private final S3Client s3;

    public S3AudioService(@Value("${aws.s3.bucket}") String bucket,
                          @Value("${aws.region}") String region) {
        this.bucket = bucket;
        var creds = DefaultCredentialsProvider.create();
        var reg = Region.of(region);

        this.presigner = S3Presigner.builder()
                .region(reg)
                .credentialsProvider(creds)
                .build();

        this.s3 = S3Client.builder()
                .region(reg)
                .credentialsProvider(creds)
                .build();
    }

    public URL generatePresignedUrl(String s3Key, Duration duration) {
        if (duration == null || duration.isNegative() || duration.isZero()) {
            duration = Duration.ofMinutes(10);
        } else if (duration.compareTo(Duration.ofDays(7)) > 0) {
            duration = Duration.ofDays(7); // S3 max
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration) // <-- use the parameter
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url();
    }

    public List<String> listKeys(String prefix) {
        var it = software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable
                .fromClient(s3, b -> b.bucket(bucket).prefix(prefix == null ? "" : prefix));

        List<String> keys = new ArrayList<>();
        for (var page : it) {
            page.contents().forEach(obj -> {
                if (!obj.key().endsWith("/")) keys.add(obj.key());
            });
        }
        return keys;
    }

    @PreDestroy
    public void close() {
        presigner.close();
        s3.close();
    }
}
