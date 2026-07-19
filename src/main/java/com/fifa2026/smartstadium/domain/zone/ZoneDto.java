package com.fifa2026.smartstadium.domain.zone;

import java.time.Instant;
import java.util.UUID;

public record ZoneDto(
    UUID id,
    UUID stadiumId,
    String name,
    String type,
    int capacity,
    Instant createdAt,
    Instant updatedAt
) {}
