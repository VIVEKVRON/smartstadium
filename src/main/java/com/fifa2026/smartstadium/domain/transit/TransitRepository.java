package com.fifa2026.smartstadium.domain.transit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransitRepository extends JpaRepository<TransitSchedule, UUID> {
    List<TransitSchedule> findByStadiumId(UUID stadiumId);
    List<TransitSchedule> findByStadiumIdAndTransportType(UUID stadiumId, String transportType);
}
