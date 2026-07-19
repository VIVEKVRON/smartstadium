package com.fifa2026.smartstadium.domain.assistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "app.gemini.enabled", havingValue = "true")
public class GeminiLLMService implements LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiLLMService.class);

    @Override
    public String generateResponse(String query, List<String> context, String language) {
        logger.info("Generating response via Gemini LLM for language: {}", language);
        // Placeholder for real Gemini API integration
        return "Gemini Response [" + language + "]: Based on context, " + query;
    }
}
