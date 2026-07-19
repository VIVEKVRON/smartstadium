package com.fifa2026.smartstadium.domain.zone;

import com.fifa2026.smartstadium.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ZoneService {
    private static final Logger logger = LoggerFactory.getLogger(ZoneService.class);
    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<ZoneDto> getZonesByStadium(UUID stadiumId) {
        logger.info("Fetching zones for stadium id: {}", stadiumId);
        return zoneRepository.findByStadiumId(stadiumId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ZoneDto getZoneById(UUID id) {
        logger.info("Fetching zone with id: {}", id);
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", id));
        return mapToDto(zone);
    }

    private ZoneDto mapToDto(Zone zone) {
        return new ZoneDto(
                zone.getId(),
                zone.getStadium().getId(),
                zone.getName(),
                zone.getType(),
                zone.getCapacity(),
                zone.getCreatedAt(),
                zone.getUpdatedAt()
        );
    }
}
