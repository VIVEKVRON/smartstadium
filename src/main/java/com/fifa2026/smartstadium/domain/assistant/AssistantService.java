package com.fifa2026.smartstadium.domain.assistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AssistantService.class);
    
    private final VectorSearchService vectorSearchService;
    private final LLMService llmService;

    public AssistantService(VectorSearchService vectorSearchService, LLMService llmService) {
        this.vectorSearchService = vectorSearchService;
        this.llmService = llmService;
    }

    public ChatResponse chat(ChatRequest request) {
        logger.info("Processing chat request for stadium: {}, query: {}", request.stadiumId(), request.query());
        
        // 1. Generate embedding
        float[] queryEmbedding = vectorSearchService.generateQueryEmbedding(request.query());
        
        // 2. Search similar contexts
        List<KnowledgeEmbedding> similarEmbeddings = vectorSearchService.searchSimilar(request.stadiumId(), queryEmbedding, 3);
        
        // 3. Extract context texts
        List<String> context = similarEmbeddings.stream()
                .map(KnowledgeEmbedding::getContentText)
                .collect(Collectors.toList());
                
        // 4. Generate LLM response
        String answer = llmService.generateResponse(request.query(), context, request.language());
        
        return new ChatResponse(answer, context, request.language(), Instant.now());
    }
}
