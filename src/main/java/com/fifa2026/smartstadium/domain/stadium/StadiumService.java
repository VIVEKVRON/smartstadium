package com.fifa2026.smartstadium.domain.stadium;

import com.fifa2026.smartstadium.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StadiumService {

    private static final Logger logger = LoggerFactory.getLogger(StadiumService.class);
    private final StadiumRepository stadiumRepository;

    public StadiumService(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    @Cacheable("stadiums")
    public List<StadiumDto> getAllStadiums() {
        logger.info("Fetching all stadiums");
        return stadiumRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public StadiumDto getStadiumById(UUID id) {
        logger.info("Fetching stadium with id: {}", id);
        Stadium stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stadium", "id", id));
        return mapToDto(stadium);
    }

    public List<StadiumDto> searchByName(String name) {
        logger.info("Searching stadiums by name: {}", name);
        return stadiumRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private StadiumDto mapToDto(Stadium stadium) {
        return new StadiumDto(
                stadium.getId(),
                stadium.getName(),
                stadium.getCity(),
                stadium.getCountry(),
                stadium.getCapacity(),
                stadium.getLatitude(),
                stadium.getLongitude(),
                stadium.getAccessibilityFeatures(),
                stadium.getSustainabilityInfo(),
                stadium.getMapUrl(),
                stadium.getCreatedAt(),
                stadium.getUpdatedAt()
        );
    }
}
