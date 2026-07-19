package com.fifa2026.smartstadium.domain.assistant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmbeddingRepository extends JpaRepository<KnowledgeEmbedding, UUID> {
    
    @Query(value = "SELECT * FROM knowledge_embeddings WHERE stadium_id = :stadiumId ORDER BY embedding <=> cast(:queryVector as vector) LIMIT :limit", nativeQuery = true)
    List<KnowledgeEmbedding> findSimilar(@Param("stadiumId") UUID stadiumId, @Param("queryVector") String queryVector, @Param("limit") int limit);
}
