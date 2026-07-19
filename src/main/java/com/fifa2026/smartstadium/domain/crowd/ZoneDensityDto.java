package com.fifa2026.smartstadium.domain.crowd;

import java.time.Instant;
import java.util.UUID;

public record ZoneDensityDto(
    UUID zoneId,
    String zoneName,
    double currentDensity,
    int peopleCount,
    String alertLevel,
    Instant detectedAt
) {}
