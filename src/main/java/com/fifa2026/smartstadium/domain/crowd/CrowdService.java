package com.fifa2026.smartstadium.domain.crowd;

import com.fifa2026.smartstadium.domain.stadium.Stadium;
import com.fifa2026.smartstadium.domain.stadium.StadiumRepository;
import com.fifa2026.smartstadium.domain.zone.Zone;
import com.fifa2026.smartstadium.domain.zone.ZoneRepository;
import com.fifa2026.smartstadium.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CrowdService {

    private static final Logger logger = LoggerFactory.getLogger(CrowdService.class);
    
    private final CrowdMetricRepository crowdMetricRepository;
    private final ZoneRepository zoneRepository;
    private final StadiumRepository stadiumRepository;

    public CrowdService(CrowdMetricRepository crowdMetricRepository, ZoneRepository zoneRepository, StadiumRepository stadiumRepository) {
        this.crowdMetricRepository = crowdMetricRepository;
        this.zoneRepository = zoneRepository;
        this.stadiumRepository = stadiumRepository;
    }

    public void ingestMetric(CrowdIngestRequest request) {
        logger.info("Ingesting metric for zone id: {}", request.zoneId());
        
        Zone zone = zoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "id", request.zoneId()));
                
        Stadium stadium = stadiumRepository.findById(request.stadiumId())
                .orElseThrow(() -> new ResourceNotFoundException("Stadium", "id", request.stadiumId()));

        CrowdMetric metric = new CrowdMetric();
        metric.setZone(zone);
        metric.setStadium(stadium);
        metric.setPeopleCount(request.peopleCount());
        
        double density = (double) request.peopleCount() / zone.getCapacity();
        metric.setCurrentDensity(density);
        
        String alertLevel = "NORMAL";
        if (density > 0.85) alertLevel = "CRITICAL";
        else if (density > 0.7) alertLevel = "HIGH";
        else if (density > 0.5) alertLevel = "ELEVATED";
        
        metric.setAlertLevel(alertLevel);
        metric.setCameraSource(request.cameraSource());
        metric.setDetectedAt(request.detectedAt() != null ? request.detectedAt() : Instant.now());
        
        crowdMetricRepository.save(metric);
        logger.debug("Saved metric for zone {} with density {}", zone.getName(), density);
    }

    public CrowdDashboardResponse getDashboard(UUID stadiumId) {
        logger.info("Generating dashboard for stadium id: {}", stadiumId);
        List<CrowdMetric> latestMetrics = crowdMetricRepository.findLatestMetricsPerZone(stadiumId);
        
        int totalAttendance = 0;
        double totalDensity = 0.0;
        int activeAlerts = 0;
        
        List<ZoneDensityDto> zoneDensities = latestMetrics.stream().map(m -> {
            return new ZoneDensityDto(
                m.getZone().getId(),
                m.getZone().getName(),
                m.getCurrentDensity(),
                m.getPeopleCount(),
                m.getAlertLevel(),
                m.getDetectedAt()
            );
        }).collect(Collectors.toList());

        for (ZoneDensityDto dto : zoneDensities) {
            totalAttendance += dto.peopleCount();
            totalDensity += dto.currentDensity();
            if ("HIGH".equals(dto.alertLevel()) || "CRITICAL".equals(dto.alertLevel())) {
                activeAlerts++;
            }
        }
        
        double averageDensity = zoneDensities.isEmpty() ? 0.0 : totalDensity / zoneDensities.size();
        
        return new CrowdDashboardResponse(
            stadiumId,
            totalAttendance,
            averageDensity,
            activeAlerts,
            zoneDensities
        );
    }

    public List<AlertDto> getActiveAlerts(UUID stadiumId) {
        logger.info("Fetching active alerts for stadium id: {}", stadiumId);
        return crowdMetricRepository.findLatestMetricsPerZone(stadiumId).stream()
            .filter(m -> "HIGH".equals(m.getAlertLevel()) || "CRITICAL".equals(m.getAlertLevel()))
            .map(m -> new AlertDto(m.getZone().getId(), m.getZone().getName(), m.getAlertLevel(), m.getCurrentDensity(), m.getDetectedAt()))
            .collect(Collectors.toList());
    }
}
