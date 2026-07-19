package com.fifa2026.smartstadium.domain.transit;

import com.fifa2026.smartstadium.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransitService {
    private static final Logger logger = LoggerFactory.getLogger(TransitService.class);
    private final TransitRepository transitRepository;

    public TransitService(TransitRepository transitRepository) {
        this.transitRepository = transitRepository;
    }

    @Cacheable(value = "transit-schedules", key = "#stadiumId")
    public List<TransitDto> getSchedulesByStadium(UUID stadiumId) {
        logger.info("Fetching transit schedules for stadium: {}", stadiumId);
        return transitRepository.findByStadiumId(stadiumId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TransitDto> getSchedulesByStadiumAndType(UUID stadiumId, String type) {
        logger.info("Fetching transit schedules for stadium: {} and type: {}", stadiumId, type);
        return transitRepository.findByStadiumIdAndTransportType(stadiumId, type).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TransitDto getScheduleById(UUID id) {
        logger.info("Fetching transit schedule by id: {}", id);
        TransitSchedule schedule = transitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransitSchedule", "id", id));
        return mapToDto(schedule);
    }

    private TransitDto mapToDto(TransitSchedule s) {
        return new TransitDto(
                s.getId(),
                s.getStadium().getId(),
                s.getTransportType(),
                s.getRouteName(),
                s.getStationName(),
                s.getDepartureTime(),
                s.getArrivalTime(),
                s.getStatus(),
                s.getEstimatedDelayMinutes()
        );
    }
}
