package com.example.api.service;

import com.example.api.entity.Track;
import com.example.api.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import jakarta.transaction.Transactional;
import java.util.Objects;

@Service
public class TrackImportService {
    private final S3Client s3Client;
    private final TrackRepository trackRepository;
    private final String bucket;
    
    public TrackImportService(TrackRepository trackRepository, S3Client s3Client, @Value("${aws.s3.bucket}") String bucket){
        this.trackRepository = trackRepository;
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Transactional
    public int importPrefix(String prefix) {
        int imported = 0;
        var pages = s3Client.listObjectsV2Paginator(b -> b.bucket(bucket).prefix(prefix == null ? "" : prefix.trim()));
        for (var page : pages) {
            for (var obj : page.contents()) {
                String key = obj.key();
                if (key.endsWith("/")) continue;

                var head = s3Client.headObject(b -> b.bucket(bucket).key(key));
                var existing = trackRepository.findByS3Key(key).orElse(null);

                String[] parsed = parse(key);
                String artist = parsed[0];
                String title  = parsed[1];

                if (existing == null) {
                    var t = new Track();
                    t.setS3Key(key);
                    t.setTrackArtist(artist);
                    t.setTrackName(title);
                    trackRepository.save(t);
                    imported++;
                } else {
                    boolean changed = false;
                    if (!Objects.equals(existing.getTrackArtist(), artist)) {
                        existing.setTrackArtist(artist);
                        changed = true;
                    }
                    if (!Objects.equals(existing.getTrackName(), title)) {
                        existing.setTrackName(title);
                        changed = true;
                    }
                    if (changed) {
                        trackRepository.save(existing);
                        imported++;
                    }
                }
            }
        }
        return imported;
    }

    private static String[] parse(String key) {
        String base = key.substring(key.lastIndexOf('/') + 1);
        int dot = base.lastIndexOf('.');
        if (dot > 0) base = base.substring(0, dot);
        String artist = null, title = base;
        int sep = base.indexOf(" - ");
        if (sep > 0) { artist = base.substring(0, sep).trim(); title = base.substring(sep + 3).trim(); }
        return new String[]{artist, title};
    }
}
