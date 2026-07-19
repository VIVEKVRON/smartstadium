package com.fifa2026.smartstadium.domain.crowd;

import java.time.Instant;
import java.util.UUID;

public record AlertDto(
    UUID zoneId,
    String zoneName,
    String alertLevel,
    double density,
    Instant detectedAt
) {}
