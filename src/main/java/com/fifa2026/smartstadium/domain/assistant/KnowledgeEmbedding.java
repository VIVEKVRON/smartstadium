package com.fifa2026.smartstadium.domain.assistant;

import com.fifa2026.smartstadium.domain.stadium.Stadium;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "knowledge_embeddings")
public class KnowledgeEmbedding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    private String contentType;

    @Column(columnDefinition = "text")
    private String contentText;

    private String language;

    @Column(columnDefinition = "vector(768)")
    private float[] embedding;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Stadium getStadium() { return stadium; }
    public void setStadium(Stadium stadium) { this.stadium = stadium; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
}
