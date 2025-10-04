package com.example.api.repository;

import com.example.api.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Integer>{

    List<Track> findByTrackArtist(String trackArtist);
    Optional<Track> findByS3Key(String s3Key);
    boolean existsByS3Key(String s3Key);

}
