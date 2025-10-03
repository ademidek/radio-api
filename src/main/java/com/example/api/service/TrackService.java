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

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

}
