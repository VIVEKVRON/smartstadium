package com.fifa2026.smartstadium.domain.assistant;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
    String answer,
    List<String> sources,
    String language,
    Instant timestamp
) {}
