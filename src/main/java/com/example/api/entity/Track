package com.example.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="trackId")
    private Integer trackId;

    @Column (name="trackArtist")
    private String trackArtist;

    @Column (name="trackName")
    private String trackName;

    public Track(){}

    public Track(String trackName, String trackArtist) {
        this.trackName = trackName;
        this.trackArtist = trackArtist;
    }

    public Track(Integer trackId, String trackName, String trackArtist) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getTrackName(){
        return trackName;
    }

    public void setTrackName(String trackName){
        this.trackName = trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        Track other = (Track) o;
        return trackId != null && trackId.equals(other.trackId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Track: " + trackName +
                ", trackId=" + trackId +
                ", trackArtist=" + trackArtist;
    }


}