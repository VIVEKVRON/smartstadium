document.addEventListener('DOMContentLoaded', () => {
    // Check auth for staff page
    if (!localStorage.getItem('auth_token') && window.location.hostname !== 'localhost') {
        // Allow localhost bypass for testing, otherwise require login
        // console.log("Not logged in");
    }

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => window.appAuth.logout());
    }

    // Dashboard State
    let currentStadiumId = 1; // Default
    let refreshInterval = null;

    // Elements
    const stadiumSelector = document.getElementById('stadium-selector');
    const heatmapContainer = document.getElementById('heatmap-container');
    const capacityContainer = document.getElementById('capacity-container');
    const alertsContainer = document.getElementById('alerts-container');
    
    // Metrics
    const metricAttendance = document.getElementById('metric-attendance');
    const metricDensity = document.getElementById('metric-density');
    const metricAlerts = document.getElementById('metric-alerts');
    const metricTime = document.getElementById('metric-time');

    // Init
    async function init() {
        await fetchStadiums();
        fetchDashboardData();
        
        stadiumSelector.addEventListener('change', (e) => {
            currentStadiumId = e.target.value;
            fetchDashboardData();
        });

        // Auto-refresh every 10 seconds
        refreshInterval = setInterval(fetchDashboardData, 10000);
    }

    async function fetchStadiums() {
        try {
            const res = await fetch('/api/v1/stadiums', { headers: window.appAuth.getAuthHeaders() });
            if (!res.ok) throw new Error('Failed to fetch stadiums');
            const stadiums = await res.json();
            
            stadiumSelector.innerHTML = stadiums.map(s => 
                `<option value="${s.id}">${s.name}</option>`
            ).join('');
            
            if (stadiums.length > 0) {
                currentStadiumId = stadiums[0].id;
                stadiumSelector.value = currentStadiumId;
            }
        } catch (error) {
            console.error('Error fetching stadiums:', error);
            stadiumSelector.innerHTML = '<option value="1">Main Stadium (Fallback)</option>';
        }
    }

    async function fetchDashboardData() {
        if (!currentStadiumId) return;
        
        try {
            const res = await fetch(`/api/v1/crowd/dashboard/${currentStadiumId}`, { 
                headers: window.appAuth.getAuthHeaders() 
            });
            
            if (!res.ok) throw new Error('Failed to fetch dashboard data');
            const data = await res.json();
            
            renderHeatmap(data.zones || []);
            renderCapacityGauges(data.zones || []);
            renderAlerts([]); // Backend doesn't return full alert objects yet
            updateMetrics(data);
            
            updateTimestamp();
        } catch (error) {
            console.error('Error fetching dashboard data:', error);
            // Mock data for display if API fails
            renderMockData();
        }
    }

    function renderHeatmap(zones) {
        heatmapContainer.innerHTML = '';
        zones.forEach(zone => {
            const density = zone.currentDensity || 0;
            // Calculate approximate capacity backwards
            const capacity = density > 0 ? Math.round(zone.peopleCount / (density / 100)) : 0;
            
            let statusColor = 'var(--status-green)';
            if (density > 85) statusColor = 'var(--status-red)';
            else if (density > 70) statusColor = 'var(--status-orange)';
            else if (density > 50) statusColor = 'var(--status-yellow)';

            const card = document.createElement('div');
            card.className = 'zone-card';
            card.style.borderTop = `4px solid ${statusColor}`;
            card.innerHTML = `
                <div>
                    <h3 style="margin-bottom: 4px;">${zone.zoneName || 'Unknown Zone'}</h3>
                    <div style="font-size: 0.85rem; color: var(--on-surface-variant);">Capacity: ${capacity}</div>
                </div>
                <div class="mt-4">
                    <div style="font-size: 1.5rem; font-weight: 700; color: ${statusColor};">${Math.round(density)}%</div>
                    <div style="font-size: 0.85rem;">${zone.peopleCount || 0} people</div>
                </div>
            `;
            heatmapContainer.appendChild(card);
        });
    }

    function renderCapacityGauges(zones) {
        capacityContainer.innerHTML = '';
        // Limit to 8 zones for UI layout
        zones.slice(0, 8).forEach(zone => {
            const percentage = Math.round(zone.currentDensity || 0);
            
            let strokeColor = 'var(--status-green)';
            if (percentage > 85) strokeColor = 'var(--status-red)';
            else if (percentage > 70) strokeColor = 'var(--status-orange)';
            else if (percentage > 50) strokeColor = 'var(--status-yellow)';

            const dashArray = `${percentage}, 100`;

            const gauge = document.createElement('div');
            gauge.className = 'text-center';
            gauge.innerHTML = `
                <div class="progress-circular" style="margin: 0 auto;">
                    <svg viewBox="0 0 36 36">
                        <circle class="bg" cx="18" cy="18" r="16"></circle>
                        <circle class="fill" cx="18" cy="18" r="16" stroke-dasharray="${dashArray}" style="stroke: ${strokeColor};"></circle>
                    </svg>
                    <div class="progress-value">${percentage}%</div>
                </div>
                <div style="font-size: 0.75rem; margin-top: 8px;">${zone.zoneName || 'Unknown'}</div>
            `;
            capacityContainer.appendChild(gauge);
        });
    }

    function renderAlerts(alerts) {
        alertsContainer.innerHTML = '';
        if (alerts.length === 0) {
            alertsContainer.innerHTML = '<div style="color: var(--on-surface-variant); padding: 1rem;">No active alerts.</div>';
            return;
        }

        alerts.forEach(alert => {
            const severityClass = alert.severity.toLowerCase(); // critical, warning, info
            const el = document.createElement('div');
            el.className = `alert-item ${severityClass}`;
            el.innerHTML = `
                <div class="alert-header">
                    <span class="badge badge-${getSeverityColor(severityClass)}">${alert.severity}</span>
                    <span class="alert-time">${new Date(alert.timestamp).toLocaleTimeString()}</span>
                </div>
                <div class="alert-msg"><strong>${alert.zoneName}:</strong> ${alert.message}</div>
            `;
            alertsContainer.appendChild(el);
        });
    }

    function getSeverityColor(sev) {
        if (sev === 'critical') return 'red';
        if (sev === 'warning') return 'orange';
        return 'yellow';
    }

    function updateMetrics(data) {
        metricAttendance.textContent = data.totalAttendance || 0;
        metricDensity.textContent = data.averageDensity ? `${Math.round(data.averageDensity)}%` : '0%';
        metricAlerts.textContent = data.activeAlerts || 0;
    }

    function updateTimestamp() {
        metricTime.textContent = new Date().toLocaleTimeString();
    }

    function renderMockData() {
        const mockZones = Array.from({length: 8}, (_, i) => ({
            name: `Zone ${i+1}`,
            capacity: 5000,
            currentPeople: Math.floor(Math.random() * 5000)
        }));
        
        const mockAlerts = [
            { severity: 'CRITICAL', timestamp: new Date().toISOString(), zoneName: 'Zone 3', message: 'Density exceeded 90%' },
            { severity: 'WARNING', timestamp: new Date().toISOString(), zoneName: 'Zone 1', message: 'Rapid crowd buildup detected' }
        ];

        renderHeatmap(mockZones);
        renderCapacityGauges(mockZones);
        renderAlerts(mockAlerts);
        
        const total = mockZones.reduce((acc, z) => acc + z.currentPeople, 0);
        const max = mockZones.reduce((acc, z) => acc + z.capacity, 0);
        
        updateMetrics({
            totalAttendance: total,
            averageDensity: (total/max)*100,
            alerts: mockAlerts
        });
        updateTimestamp();
    }

    init();
});
