package com.example.api.repository;

import com.example.api.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Integer>{

    List<Track> findByTrackArtist(String trackArtist);

}
