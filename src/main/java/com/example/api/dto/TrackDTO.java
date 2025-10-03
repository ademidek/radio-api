package com.example.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TrackDTO {

    @NotBlank
    @Size(max = 100)
    private String trackName;
    
    @NotBlank
    @Size(max = 100)
    private String trackArtist;

    public TrackDTO(){}

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }
    
}