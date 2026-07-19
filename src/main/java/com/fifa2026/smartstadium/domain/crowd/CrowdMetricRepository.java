package com.fifa2026.smartstadium.domain.crowd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CrowdMetricRepository extends JpaRepository<CrowdMetric, UUID> {
    List<CrowdMetric> findByStadiumIdOrderByDetectedAtDesc(UUID stadiumId);
    
    CrowdMetric findTopByZoneIdOrderByDetectedAtDesc(UUID zoneId);

    @Query(value = "SELECT DISTINCT ON (zone_id) * FROM crowd_metrics WHERE stadium_id = :stadiumId ORDER BY zone_id, detected_at DESC", nativeQuery = true)
    List<CrowdMetric> findLatestMetricsPerZone(@Param("stadiumId") UUID stadiumId);
}
