package com.fifa2026.smartstadium.domain.crowd;

import com.fifa2026.smartstadium.domain.stadium.Stadium;
import com.fifa2026.smartstadium.domain.zone.Zone;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "crowd_metrics")
public class CrowdMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    private double currentDensity;
    private int peopleCount;
    private String cameraSource;
    private String alertLevel;
    
    private Instant detectedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }
    public Stadium getStadium() { return stadium; }
    public void setStadium(Stadium stadium) { this.stadium = stadium; }
    public double getCurrentDensity() { return currentDensity; }
    public void setCurrentDensity(double currentDensity) { this.currentDensity = currentDensity; }
    public int getPeopleCount() { return peopleCount; }
    public void setPeopleCount(int peopleCount) { this.peopleCount = peopleCount; }
    public String getCameraSource() { return cameraSource; }
    public void setCameraSource(String cameraSource) { this.cameraSource = cameraSource; }
    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }
    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }
}
