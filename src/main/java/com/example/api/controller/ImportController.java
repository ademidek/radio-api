package com.example.api.controller;

import com.example.api.service.TrackImportService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/import")
public class ImportController {

    private final TrackImportService importService;

    public ImportController(TrackImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/s3")
    public Map<String, Object> importTracksFromS3(@RequestParam(defaultValue = "") String prefix) {
        var result = importService.importPrefix(prefix);
        return Map.of(
            "created", result.created(),
            "updated", result.updated(),
            "prefix", prefix
        );
    }
}
