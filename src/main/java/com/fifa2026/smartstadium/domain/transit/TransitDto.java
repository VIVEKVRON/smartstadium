package com.fifa2026.smartstadium.domain.transit;

import java.time.Instant;
import java.util.UUID;

public record TransitDto(
    UUID id,
    UUID stadiumId,
    String transportType,
    String routeName,
    String stationName,
    Instant departureTime,
    Instant arrivalTime,
    String status,
    int estimatedDelayMinutes
) {}
