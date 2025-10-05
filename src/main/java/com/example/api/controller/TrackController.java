package com.example.api.controller;

import com.example.api.entity.Track;
import com.example.api.repository.TrackRepository;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.api.service.S3AudioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tracks")
public class TrackController {
    @GetMapping("/")
    public String index(){
        return "Welcome to AK(tion) Radio.";
    }

    private final TrackRepository trackRepository;
    private final S3AudioService s3AudioService;
    
    public TrackController(TrackRepository trackRepository, S3AudioService s3AudioService) {
        this.trackRepository = trackRepository;
        this.s3AudioService = s3AudioService;
    }

    @GetMapping("/tracks")
    public String showTracks(){
        return "Showing all tracks";
    }

    @GetMapping("/tracks/{id}")
    public Map<String, Object> generatePresignedUrl(@PathVariable Integer id,
                                               @RequestParam(defaultValue = "10") int minutes) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Track " + id + " not found"));
        URL url = s3AudioService.generatePresignedUrl(track.getS3Key());
        return Map.of("url", url.toString());
    }

    /*@GetMapping
    public List<Map<String,Object>> list() {
    return trackRepository.findAll().stream()
      .map(t -> Map.of(
        "id", t.getTrackId(),
        "name", t.getTrackName(),
        "artist", t.getTrackArtist(),
        "s3Key", t.getS3Key()))
      .toList();
  }*/
}

