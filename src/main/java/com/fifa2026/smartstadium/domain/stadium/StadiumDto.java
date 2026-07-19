package com.fifa2026.smartstadium.domain.stadium;

import java.time.Instant;
import java.util.UUID;

public record StadiumDto(
    UUID id,
    String name,
    String city,
    String country,
    int capacity,
    Double latitude,
    Double longitude,
    String accessibilityFeatures,
    String sustainabilityInfo,
    String mapUrl,
    Instant createdAt,
    Instant updatedAt
) {}
