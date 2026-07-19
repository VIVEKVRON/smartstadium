package com.fifa2026.smartstadium.domain.assistant;

import java.util.List;

public interface LLMService {
    String generateResponse(String query, List<String> context, String language);
}
