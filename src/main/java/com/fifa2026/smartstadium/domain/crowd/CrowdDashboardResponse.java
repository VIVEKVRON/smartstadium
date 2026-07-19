package com.fifa2026.smartstadium.domain.crowd;

import java.util.List;
import java.util.UUID;

public record CrowdDashboardResponse(
    UUID stadiumId,
    int totalAttendance,
    double averageDensity,
    int activeAlerts,
    List<ZoneDensityDto> zones
) {}
