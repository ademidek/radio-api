package com.example.api.service;

import com.example.api.entity.Track;
import com.example.api.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;
    
    @Autowired
    private S3AudioService s3AudioService;

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Track getById(Integer id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Track not found with id: " + id));
    }

    public Track getByS3Key(String s3Key) {
        return trackRepository.findByS3Key(s3Key)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Track not found with s3Key: " + s3Key));
    }

    public java.net.URL getPlaybackUrl(Integer id, java.time.Duration duration) {
        Track track = getById(id);
        
        String key = track.getS3Key();

        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Track " + id + " has no S3 key configured");
        }

        return s3AudioService.generatePresignedUrl(key, duration);
    }
}
