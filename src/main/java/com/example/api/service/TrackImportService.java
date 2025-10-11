package com.example.api.service;

import com.example.api.entity.Track;
import com.example.api.repository.TrackRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class TrackImportService {

    private static final Set<String> AUDIO_EXTS =
            Set.of(".mp3", ".wav", ".flac", ".m4a", ".aac", ".ogg");

    private final S3Client s3;
    private final TrackRepository repo;
    private final String bucket;

    public TrackImportService(TrackRepository repo,
                              S3Client s3,
                              @Value("${aws.s3.bucket}") String bucket) {
        this.repo = repo;
        this.s3 = s3;
        this.bucket = bucket;
    }

    public record ImportResult(int created, int updated) {}

    @Transactional
    public ImportResult importPrefix(String prefix) {
        final String prefixNorm = normalizePrefix(prefix);

        int created = 0;
        int updated = 0;

        try {
            var pages = s3.listObjectsV2Paginator(b -> b.bucket(bucket).prefix(prefixNorm));
            // Alternatively (no lambda):
            // var req = ListObjectsV2Request.builder().bucket(bucket).prefix(prefixNorm).build();
            // var pages = s3.listObjectsV2Paginator(req);

            for (var page : pages) {
                for (var obj : page.contents()) {
                    final String key = obj.key();
                    if (key == null || key.endsWith("/") || !isAudio(key)) continue;

                    // Verifying permissions without lambda:
                    // s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());

                    var existing = repo.findByS3Key(key).orElse(null);

                    var parsed = parseFromKey(key);
                    String artist = parsed.artist();
                    String title  = parsed.title();

                    if (artist == null || artist.isBlank()) artist = "Unknown";

                    if (existing == null) {
                        var t = new Track();
                        t.setS3Key(key);
                        t.setTrackArtist(artist);
                        t.setTrackName(title);
                        repo.save(t);
                        created++;
                    } else {
                        boolean changed = false;
                        if (!Objects.equals(existing.getTrackArtist(), artist)) { existing.setTrackArtist(artist); changed = true; }
                        if (!Objects.equals(existing.getTrackName(), title))    { existing.setTrackName(title);    changed = true; }
                        if (changed) { repo.save(existing); updated++; }
                    }
                }
            }
            return new ImportResult(created, updated);
        } catch (S3Exception | SdkClientException e) {
            throw new RuntimeException("Failed importing from s3://" + bucket + "/" + prefixNorm + ": " + e.getMessage(), e);
        }
    }

    private static String normalizePrefix(String prefix) {
        String p = (prefix == null) ? "" : prefix.trim();
        if (p.startsWith("/")) p = p.substring(1);
        return p;
    }


    private static boolean isAudio(String key) {
        String lower = key.toLowerCase(Locale.ROOT);
        return AUDIO_EXTS.stream().anyMatch(lower::endsWith);
    }

    // Parses "Artist - Title.ext" or "Artist/Title.ext" into fields
    private static Parsed parseFromKey(String key) {
        String base = key.substring(key.lastIndexOf('/') + 1);
        int dot = base.lastIndexOf('.');
        if (dot > 0) base = base.substring(0, dot);
        String artist = null, title = base;
        int sep = base.indexOf(" - ");
        if (sep > 0) {
            artist = base.substring(0, sep).trim();
            title = base.substring(sep + 3).trim();
        }
        return new Parsed(artist, title);
    }

    private record Parsed(String artist, String title) {}
}
