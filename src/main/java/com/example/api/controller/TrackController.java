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
import java.util.Map;

@RestController
public class TrackController {
    @GetMapping("/")
    public String index(){
        return "Hello World!";
    }

    private final TrackRepository trackRepository;
    private final S3AudioService s3AudioService;
    
    public TrackController(TrackRepository trackRepository, S3AudioService s3AudioService) {
        this.trackRepository = trackRepository;
        this.s3AudioService = s3AudioService;
    }

    @GetMapping("/tracks/{id}")
    public Map<String, Object> generatePresignedUrl(@PathVariable Integer id,
                                               @RequestParam(defaultValue = "10") int minutes) {
        Track track = trackRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Track " + id + " not found"));
        URL url = s3AudioService.generatePresignedUrl(track.getS3Key());
        return Map.of("url", url.toString());

}
}

