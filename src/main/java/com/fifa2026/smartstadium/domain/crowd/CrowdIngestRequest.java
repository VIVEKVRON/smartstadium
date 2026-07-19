package com.fifa2026.smartstadium.domain.crowd;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CrowdIngestRequest(
    @NotNull UUID zoneId,
    @NotNull UUID stadiumId,
    @Min(0) int peopleCount,
    String cameraSource,
    Instant detectedAt
) {}
