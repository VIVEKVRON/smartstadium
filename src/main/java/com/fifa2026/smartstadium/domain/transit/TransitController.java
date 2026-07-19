package com.fifa2026.smartstadium.domain.transit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transit")
public class TransitController {
    
    private final TransitService transitService;

    public TransitController(TransitService transitService) {
        this.transitService = transitService;
    }

    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<List<TransitDto>> getSchedulesByStadium(@PathVariable UUID stadiumId) {
        return ResponseEntity.ok(transitService.getSchedulesByStadium(stadiumId));
    }

    @GetMapping("/stadium/{stadiumId}/type/{transportType}")
    public ResponseEntity<List<TransitDto>> getSchedulesByType(@PathVariable UUID stadiumId, @PathVariable String transportType) {
        return ResponseEntity.ok(transitService.getSchedulesByStadiumAndType(stadiumId, transportType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransitDto> getScheduleById(@PathVariable UUID id) {
        return ResponseEntity.ok(transitService.getScheduleById(id));
    }
}
