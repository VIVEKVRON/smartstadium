package com.fifa2026.smartstadium.domain.assistant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChatRequest(
    @NotBlank String query,
    @NotNull UUID stadiumId,
    @NotBlank String language
) {}
