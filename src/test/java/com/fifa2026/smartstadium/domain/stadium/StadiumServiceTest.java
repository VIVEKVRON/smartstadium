package com.fifa2026.smartstadium.domain.stadium;

import com.fifa2026.smartstadium.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StadiumServiceTest {

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private StadiumService stadiumService;

    private UUID stadiumId;
    private Stadium stadium;

    @BeforeEach
    void setUp() {
        stadiumId = UUID.randomUUID();
        stadium = new Stadium();
        stadium.setId(stadiumId);
        stadium.setName("MetLife Stadium");
        stadium.setCity("East Rutherford");
        stadium.setCountry("USA");
        stadium.setCapacity(82500);
    }

    @Test
    @DisplayName("Test getAllStadiums")
    void testGetAllStadiums() {
        // Arrange
        when(stadiumRepository.findAll()).thenReturn(List.of(stadium));

        // Act
        List<StadiumDto> result = stadiumService.getAllStadiums();

        // Assert
        assertEquals(1, result.size());
        assertEquals("MetLife Stadium", result.get(0).name());
        verify(stadiumRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test getStadiumById with valid ID")
    void testGetStadiumById_Valid() {
        // Arrange
        when(stadiumRepository.findById(stadiumId)).thenReturn(Optional.of(stadium));

        // Act
        StadiumDto result = stadiumService.getStadiumById(stadiumId);

        // Assert
        assertNotNull(result);
        assertEquals(stadiumId, result.id());
        assertEquals("MetLife Stadium", result.name());
        verify(stadiumRepository, times(1)).findById(stadiumId);
    }

    @Test
    @DisplayName("Test getStadiumById with invalid ID throws ResourceNotFoundException")
    void testGetStadiumById_Invalid() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(stadiumRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> stadiumService.getStadiumById(invalidId));
        verify(stadiumRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Test searchByName")
    void testSearchByName() {
        // Arrange
        when(stadiumRepository.findByNameContainingIgnoreCase("MetLife")).thenReturn(List.of(stadium));

        // Act
        List<StadiumDto> result = stadiumService.searchByName("MetLife");

        // Assert
        assertEquals(1, result.size());
        assertEquals("MetLife Stadium", result.get(0).name());
        verify(stadiumRepository, times(1)).findByNameContainingIgnoreCase("MetLife");
    }
}
