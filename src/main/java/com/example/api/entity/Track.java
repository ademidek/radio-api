package com.example.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="track_id", nullable = false)
    private Integer trackId;

    @Column (name="track_artist", nullable = false)
    private String trackArtist;

    @Column (name="track_name", nullable = false)
    private String trackName;

    @Column(name="s3_key", unique=true, length=1024, nullable = false)
    private String s3Key;

    @Column(name="duration")
    private Integer duration;

    public Track(){}

    public Track(String trackName, String trackArtist) {
        this.trackName = trackName;
        this.trackArtist = trackArtist;
    }

    public Track(Integer trackId, String trackName, String trackArtist, String s3Key) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.s3Key = s3Key;
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

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
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