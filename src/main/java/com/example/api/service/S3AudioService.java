package com.example.api.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class S3AudioService {

    private static final Duration DEFAULT_PRESIGN = Duration.ofMinutes(10);
    private static final Duration MAX_PRESIGN = Duration.ofDays(7);

    private final String bucket;
    private final S3Presigner presigner;
    private final S3Client s3;          

    public S3AudioService(@Value("${aws.s3.bucket}") String bucket,
                          S3Presigner presigner,
                          S3Client s3) {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("aws.s3.bucket is required");
        }
        this.bucket = bucket.trim();
        this.presigner = presigner;
        this.s3 = s3;
    }

    public URL generatePresignedUrl(String s3Key, Duration requested) {
        String key = Objects.requireNonNull(s3Key, "s3Key must not be null").trim();

        try {
            s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (NoSuchKeyException e) {
            throw new IllegalArgumentException("S3 object not found for key: " + key, e);
        } catch (S3Exception | SdkClientException e) {
            throw new RuntimeException("Failed to verify S3 object: " + key, e);
        }

        Duration ttl = normalizeDuration(requested);

        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest req = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(get)
                .build();

        return presigner.presignGetObject(req).url();
    }

    public List<String> listKeys(String prefix) {
        String safePrefix = (prefix == null) ? "" : prefix.trim();

        ListObjectsV2Iterable pages = s3.listObjectsV2Paginator(
                b -> b.bucket(bucket).prefix(safePrefix)
        );

        List<String> keys = new ArrayList<>();
        for (var page : pages) {
            for (var obj : page.contents()) {
                String key = obj.key();
                if (key != null && !key.endsWith("/")) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    private static Duration normalizeDuration(Duration d) {
        if (d == null || d.isZero() || d.isNegative()) return DEFAULT_PRESIGN;
        return d.compareTo(MAX_PRESIGN) > 0 ? MAX_PRESIGN : d;
    }

    @PreDestroy
    public void close() {
    }
}
