package com.fifa2026.smartstadium.domain.crowd;

import com.fifa2026.smartstadium.domain.stadium.Stadium;
import com.fifa2026.smartstadium.domain.stadium.StadiumRepository;
import com.fifa2026.smartstadium.domain.zone.Zone;
import com.fifa2026.smartstadium.domain.zone.ZoneRepository;
import com.fifa2026.smartstadium.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrowdServiceTest {

    @Mock
    private CrowdMetricRepository crowdMetricRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private StadiumRepository stadiumRepository;

    @InjectMocks
    private CrowdService crowdService;

    private UUID stadiumId;
    private UUID zoneId;
    private Stadium stadium;
    private Zone zone;

    @BeforeEach
    void setUp() {
        stadiumId = UUID.randomUUID();
        zoneId = UUID.randomUUID();

        stadium = new Stadium();
        stadium.setId(stadiumId);
        stadium.setName("MetLife Stadium");

        zone = new Zone();
        zone.setId(zoneId);
        zone.setName("North Gate");
        zone.setCapacity(100);
        zone.setStadium(stadium);
    }

    @Test
    @DisplayName("Test density alert levels calculation")
    void testDensityAlertLevels() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(stadiumRepository.findById(stadiumId)).thenReturn(Optional.of(stadium));

        // Act & Assert
        // density 0.3 -> NORMAL
        crowdService.ingestMetric(new CrowdIngestRequest(stadiumId, zoneId, 30, "cam1", Instant.now()));
        ArgumentCaptor<CrowdMetric> metricCaptor = ArgumentCaptor.forClass(CrowdMetric.class);
        verify(crowdMetricRepository, times(1)).save(metricCaptor.capture());
        assertEquals("NORMAL", metricCaptor.getValue().getAlertLevel());
        reset(crowdMetricRepository);

        // density 0.6 -> ELEVATED
        crowdService.ingestMetric(new CrowdIngestRequest(stadiumId, zoneId, 60, "cam1", Instant.now()));
        verify(crowdMetricRepository, times(1)).save(metricCaptor.capture());
        assertEquals("ELEVATED", metricCaptor.getValue().getAlertLevel());
        reset(crowdMetricRepository);

        // density 0.75 -> HIGH
        crowdService.ingestMetric(new CrowdIngestRequest(stadiumId, zoneId, 75, "cam1", Instant.now()));
        verify(crowdMetricRepository, times(1)).save(metricCaptor.capture());
        assertEquals("HIGH", metricCaptor.getValue().getAlertLevel());
        reset(crowdMetricRepository);

        // density 0.9 -> CRITICAL
        crowdService.ingestMetric(new CrowdIngestRequest(stadiumId, zoneId, 90, "cam1", Instant.now()));
        verify(crowdMetricRepository, times(1)).save(metricCaptor.capture());
        assertEquals("CRITICAL", metricCaptor.getValue().getAlertLevel());
    }

    @Test
    @DisplayName("Test ingestMetric with valid request")
    void ingestMetric_ValidRequest() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(stadiumRepository.findById(stadiumId)).thenReturn(Optional.of(stadium));
        CrowdIngestRequest request = new CrowdIngestRequest(stadiumId, zoneId, 50, "camera-A", Instant.now());

        // Act
        crowdService.ingestMetric(request);

        // Assert
        ArgumentCaptor<CrowdMetric> captor = ArgumentCaptor.forClass(CrowdMetric.class);
        verify(crowdMetricRepository).save(captor.capture());
        CrowdMetric saved = captor.getValue();
        assertEquals(50, saved.getPeopleCount());
        assertEquals(0.5, saved.getCurrentDensity());
        assertEquals("NORMAL", saved.getAlertLevel());
        assertEquals(zone, saved.getZone());
        assertEquals(stadium, saved.getStadium());
    }

    @Test
    @DisplayName("Test ingestMetric with invalid zone ID throws exception")
    void ingestMetric_InvalidZoneId() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.empty());
        CrowdIngestRequest request = new CrowdIngestRequest(stadiumId, zoneId, 50, "cam1", Instant.now());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> crowdService.ingestMetric(request));
        verify(crowdMetricRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test getDashboard returns correct aggregations")
    void getDashboard_ReturnsCorrectData() {
        // Arrange
        CrowdMetric m1 = new CrowdMetric();
        m1.setZone(zone);
        m1.setPeopleCount(30);
        m1.setCurrentDensity(0.3);
        m1.setAlertLevel("NORMAL");

        Zone zone2 = new Zone();
        zone2.setId(UUID.randomUUID());
        zone2.setName("South Gate");
        zone2.setCapacity(100);

        CrowdMetric m2 = new CrowdMetric();
        m2.setZone(zone2);
        m2.setPeopleCount(80);
        m2.setCurrentDensity(0.8);
        m2.setAlertLevel("HIGH");

        when(crowdMetricRepository.findLatestMetricsPerZone(stadiumId)).thenReturn(List.of(m1, m2));

        // Act
        CrowdDashboardResponse response = crowdService.getDashboard(stadiumId);

        // Assert
        assertEquals(stadiumId, response.stadiumId());
        assertEquals(110, response.totalAttendance());
        assertEquals(0.55, response.averageDensity(), 0.01);
        assertEquals(1, response.activeAlerts());
        assertEquals(2, response.zones().size());
    }

    @Test
    @DisplayName("Test getActiveAlerts only returns HIGH and CRITICAL")
    void getActiveAlerts_OnlyHighAndCritical() {
        // Arrange
        CrowdMetric m1 = new CrowdMetric();
        m1.setZone(zone);
        m1.setAlertLevel("NORMAL");

        Zone zone2 = new Zone();
        zone2.setId(UUID.randomUUID());
        zone2.setName("South Gate");

        CrowdMetric m2 = new CrowdMetric();
        m2.setZone(zone2);
        m2.setAlertLevel("HIGH");

        Zone zone3 = new Zone();
        zone3.setId(UUID.randomUUID());
        zone3.setName("East Gate");

        CrowdMetric m3 = new CrowdMetric();
        m3.setZone(zone3);
        m3.setAlertLevel("CRITICAL");

        when(crowdMetricRepository.findLatestMetricsPerZone(stadiumId)).thenReturn(List.of(m1, m2, m3));

        // Act
        List<AlertDto> alerts = crowdService.getActiveAlerts(stadiumId);

        // Assert
        assertEquals(2, alerts.size());
        assertTrue(alerts.stream().anyMatch(a -> a.alertLevel().equals("HIGH")));
        assertTrue(alerts.stream().anyMatch(a -> a.alertLevel().equals("CRITICAL")));
        assertFalse(alerts.stream().anyMatch(a -> a.alertLevel().equals("NORMAL")));
    }
}
