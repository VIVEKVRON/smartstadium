package com.fifa2026.smartstadium.domain.assistant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VectorSearchService.class);
    
    private final EmbeddingRepository embeddingRepository;
    
    @Value("${app.gemini.enabled:false}")
    private boolean geminiEnabled;

    public VectorSearchService(EmbeddingRepository embeddingRepository) {
        this.embeddingRepository = embeddingRepository;
    }

    public List<KnowledgeEmbedding> searchSimilar(UUID stadiumId, float[] queryVector, int limit) {
        logger.info("Searching for similar vectors in stadium: {} with limit: {}", stadiumId, limit);
        String vectorStr = "[" + java.util.stream.IntStream.range(0, queryVector.length)
                .mapToObj(i -> String.valueOf(queryVector[i]))
                .collect(Collectors.joining(",")) + "]";
        
        return embeddingRepository.findSimilar(stadiumId, vectorStr, limit);
    }

    public float[] generateQueryEmbedding(String query) {
        logger.info("Generating embedding for query: {}", query);
        if (geminiEnabled) {
            // Placeholder for Gemini Embedding API call
            return generateMockEmbedding(query);
        } else {
            return generateMockEmbedding(query);
        }
    }
    
    private float[] generateMockEmbedding(String query) {
        float[] embedding = new float[768];
        int hash = query.hashCode();
        Arrays.fill(embedding, (float) (hash % 100) / 100f);
        return embedding;
    }
}
