package com.fifa2026.smartstadium.domain.crowd;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crowd")
public class CrowdController {

    private final CrowdService crowdService;

    public CrowdController(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Void> ingestMetric(@Valid @RequestBody CrowdIngestRequest request) {
        crowdService.ingestMetric(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard/{stadiumId}")
    public ResponseEntity<CrowdDashboardResponse> getDashboard(@PathVariable UUID stadiumId) {
        return ResponseEntity.ok(crowdService.getDashboard(stadiumId));
    }

    @GetMapping("/alerts/{stadiumId}")
    public ResponseEntity<List<AlertDto>> getActiveAlerts(@PathVariable UUID stadiumId) {
        return ResponseEntity.ok(crowdService.getActiveAlerts(stadiumId));
    }
}
