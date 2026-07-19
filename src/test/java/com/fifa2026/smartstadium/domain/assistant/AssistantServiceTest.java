package com.fifa2026.smartstadium.domain.assistant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock
    private VectorSearchService vectorSearchService;

    @Mock
    private LLMService llmService;

    @InjectMocks
    private AssistantService assistantService;

    private UUID stadiumId;

    @BeforeEach
    void setUp() {
        stadiumId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Test chat orchestrates the full RAG pipeline correctly")
    void testChatPipeline() {
        // Arrange
        ChatRequest request = new ChatRequest("Where is the nearest exit?", stadiumId, "en");
        float[] fakeEmbedding = new float[]{0.1f, 0.2f, 0.3f};
        
        KnowledgeEmbedding ke1 = new KnowledgeEmbedding();
        ke1.setContentText("The nearest exit is North Gate.");
        
        KnowledgeEmbedding ke2 = new KnowledgeEmbedding();
        ke2.setContentText("South Gate is also an exit.");
        
        when(vectorSearchService.generateQueryEmbedding(request.query())).thenReturn(fakeEmbedding);
        when(vectorSearchService.searchSimilar(eq(stadiumId), eq(fakeEmbedding), eq(3)))
                .thenReturn(List.of(ke1, ke2));
        
        String llmResponse = "The nearest exits are North Gate and South Gate.";
        when(llmService.generateResponse(eq(request.query()), anyList(), eq("en"))).thenReturn(llmResponse);

        // Act
        ChatResponse response = assistantService.chat(request);

        // Assert
        verify(vectorSearchService).generateQueryEmbedding(request.query());
        verify(vectorSearchService).searchSimilar(stadiumId, fakeEmbedding, 3);
        verify(llmService).generateResponse(eq(request.query()), argThat(list -> list.contains("The nearest exit is North Gate.") && list.contains("South Gate is also an exit.")), eq("en"));
        
        assertNotNull(response);
        assertEquals(llmResponse, response.answer());
        assertEquals(2, response.sources().size());
        assertEquals("en", response.language());
        assertNotNull(response.timestamp());
    }
}
