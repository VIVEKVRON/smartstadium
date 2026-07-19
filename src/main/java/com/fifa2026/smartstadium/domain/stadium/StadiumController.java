package com.fifa2026.smartstadium.domain.stadium;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stadiums")
public class StadiumController {

    private final StadiumService stadiumService;

    public StadiumController(StadiumService stadiumService) {
        this.stadiumService = stadiumService;
    }

    @GetMapping
    public ResponseEntity<List<StadiumDto>> getAllStadiums() {
        return ResponseEntity.ok(stadiumService.getAllStadiums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StadiumDto> getStadiumById(@PathVariable UUID id) {
        return ResponseEntity.ok(stadiumService.getStadiumById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StadiumDto>> searchStadiums(@RequestParam String name) {
        return ResponseEntity.ok(stadiumService.searchByName(name));
    }
}
