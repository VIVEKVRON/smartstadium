package com.fifa2026.smartstadium.domain.stadium;

import com.fifa2026.smartstadium.domain.zone.Zone;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stadiums")
public class Stadium {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String city;
    private String country;
    private int capacity;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "jsonb")
    private String accessibilityFeatures;

    @Column(columnDefinition = "jsonb")
    private String sustainabilityInfo;

    private String mapUrl;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "stadium")
    private List<Zone> zones;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAccessibilityFeatures() { return accessibilityFeatures; }
    public void setAccessibilityFeatures(String accessibilityFeatures) { this.accessibilityFeatures = accessibilityFeatures; }
    public String getSustainabilityInfo() { return sustainabilityInfo; }
    public void setSustainabilityInfo(String sustainabilityInfo) { this. sustainabilityInfo = sustainabilityInfo; }
    public String getMapUrl() { return mapUrl; }
    public void setMapUrl(String mapUrl) { this.mapUrl = mapUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public List<Zone> getZones() { return zones; }
    public void setZones(List<Zone> zones) { this.zones = zones; }
}
