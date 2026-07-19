package com.fifa2026.smartstadium.domain.assistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "app.gemini.enabled", havingValue = "false", matchIfMissing = true)
public class MockLLMService implements LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockLLMService.class);

    @Override
    public String generateResponse(String query, List<String> context, String language) {
        logger.info("Generating mock response for language: {}", language);
        
        StringBuilder response = new StringBuilder();
        response.append("[").append(language.toUpperCase()).append("] ");
        
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("navigate") || lowerQuery.contains("directions")) {
            response.append("Directions: Head north towards the main concourse. ");
        } else if (lowerQuery.contains("accessib")) {
            response.append("Accessibility info: Elevators are located at sections A and B. ");
        } else if (lowerQuery.contains("transit") || lowerQuery.contains("train") || lowerQuery.contains("bus")) {
            response.append("Transit schedules: The next train leaves in 15 minutes. ");
        } else {
            response.append("Information: ");
        }
        
        if (!context.isEmpty()) {
            response.append("Context: ").append(String.join("; ", context));
        } else {
            response.append("No specific context found.");
        }
        
        return response.toString();
    }
}
