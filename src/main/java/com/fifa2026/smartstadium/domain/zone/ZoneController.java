package com.fifa2026.smartstadium.domain.zone;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<List<ZoneDto>> getZonesByStadium(@PathVariable UUID stadiumId) {
        return ResponseEntity.ok(zoneService.getZonesByStadium(stadiumId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDto> getZoneById(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }
}
