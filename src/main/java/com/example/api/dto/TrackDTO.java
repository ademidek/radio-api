package com.example.api.dto;

public class TrackDTO {

    @NotBlank
    @Size(max = 100)
    private String trackName;
    
    @NotBlank
    @Size(max = 100)
    private String trackArtist;

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