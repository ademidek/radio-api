package com.example.api.controller;

import com.example.api.entity.Track;
import com.example.api.service.TrackService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to AK(tion) Radio.";
    }

    @GetMapping
    public List<Track> list() {
        return trackService.getAllTracks().stream()
                .map(t -> new Track(t.getTrackId(), t.getTrackName(), t.getTrackArtist(), t.getS3Key()))
                .toList();
    }

    // Track metadata (no URL side-effects)
    @GetMapping("/{id}")
    public Track getOne(@PathVariable Integer id) {
        Track t = trackService.getById(id); 
        return new Track(
                t.getTrackId(),
                t.getTrackName(),
                t.getTrackArtist(),
                t.getS3Key()
        );
    }

    // Presigned URL endpoint
    @GetMapping("/{id}/play")
    public Map<String, Object> presigned(@PathVariable Integer id,
                                         @RequestParam(defaultValue = "10") int minutes) {
        if (minutes <= 0) minutes = 10;
        long max = 7L * 24 * 60; // 7 days
        if (minutes > max) minutes = (int) max;

        URL url = trackService.getPlaybackUrl(id, Duration.ofMinutes(minutes));
        Track t = trackService.getById(id);
        return Map.of(
                "id", t.getTrackId(),
                "name", t.getTrackName(),
                "artist", t.getTrackArtist(),
                "expiresInMinutes", minutes,
                "url", url.toString()
        );
    }

}
