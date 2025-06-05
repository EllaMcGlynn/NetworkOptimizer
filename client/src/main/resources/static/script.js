// Map to store node cards
const usageCardMap = new Map();

// Toast notification function
function showToast(message, type = 'info') {
    const toast = $('#toast');
    toast.text(message);
    toast.removeClass('info success danger').addClass(type);
    toast.fadeIn(300);
    setTimeout(() => toast.fadeOut(300), 3000);
}

// Format memory from MB to appropriate unit
function formatMemory(mb) {
    if (mb >= 1024) {
        return (mb / 1024).toFixed(1) + ' GB';
    }
    return mb + ' MB';
}

// Format timestamp to readable format
function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString() + ' ' + date.toLocaleDateString();
}

// Calculate percentage for progress bars
function calculatePercentage(used, total) {
    return Math.min(100, (used / total) * 100);
}

// Get status color based on usage percentage
function getStatusColor(percent) {
    if (percent > 85) return 'danger';
    if (percent > 70) return 'warning';
    return 'success';
}

// Create a usage card for a node
function createUsageCard(data) {
    const cpuPercent = calculatePercentage(data.cpuUsage, data.cpuAllocated);
    const memoryPercent = calculatePercentage(data.memoryUsage, data.memoryAllocated);
    const bandwidthPercent = calculatePercentage(data.bandwidthUsage, data.bandwidthAllocated);
    
    const cpuStatus = getStatusColor(cpuPercent.toFixed(1));
    const memoryStatus = getStatusColor(memoryPercent.toFixed(1));
    const bandwidthStatus = getStatusColor(bandwidthPercent.toFixed(1));

    return `
        <div class="col">
            <div class="card">
                <div class="card-header">
                    <span>Node ${data.nodeId}</span>
                </div>
                <div class="card-body">
                    <div class="metric mb-3">
                        <div class="metric-label">Network Id</div>
                        <div class="metric-value">${data.networkId}</div>
                    </div>
                    <div class="metric mb-3">
                        <div class="metric-label">CPU Usage</div>
                        <div class="metric-value">${cpuPercent.toFixed(1)}% (${data.cpuUsage.toFixed(1)} / ${data.cpuAllocated})</div>
                        <div class="progress-container">
                            <div class="progress-bar bg-${cpuStatus}" style="width: ${cpuPercent}%"></div>
                        </div>
                    </div>
                    <div class="metric mb-3">
                        <div class="metric-label">Memory Usage</div>
                        <div class="metric-value">${memoryPercent.toFixed(1)}% (${formatMemory(data.memoryUsage)} / ${formatMemory(data.memoryAllocated)})</div>
                        <div class="progress-container">
                            <div class="progress-bar bg-${memoryStatus}" style="width: ${memoryPercent}%"></div>
                        </div>
                    </div>
                    <div class="metric mb-3">
                        <div class="metric-label">Bandwidth</div>
                        <div class="metric-value">${bandwidthPercent.toFixed(1)}% (${data.bandwidthUsage.toFixed(1)} Mbps / ${data.bandwidthAllocated} Mbps)</div>
                        <div class="progress-container">
                            <div class="progress-bar bg-${bandwidthStatus}" style="width: ${bandwidthPercent}%"></div>
                        </div>
                    </div>
                    <div class="timestamp">Last updated: ${formatTimestamp(data.timestamp)}</div>
                </div>
            </div>
        </div>
    `;
}

// Create a fallback card when data can't be loaded
function createFallbackCard(nodeId) {
    return `
        <div class="col">
            <div class="card">
                <div class="card-header">
                    <span>Node ${nodeId}</span>
                    <span class="status-dot bg-danger"></span>
                </div>
                <div class="card-body">
                    <div class="text-center py-4 text-muted">
                        Failed to load node data
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Render all cards in order (0 to 4)
function renderSortedUsageCards() {
    const container = $('#nodes-container');
    container.empty();
    
    // Render cards for nodes 0 to 4 in order
    for (let i = 0; i < 5; i++) {
        if (usageCardMap.has(i)) {
            container.append(usageCardMap.get(i));
        } else {
            // If we don't have data yet, show a loading card
            container.append(`
                <div class="col">
                    <div class="card">
                        <div class="card-header">Node ${i}</div>
                        <div class="card-body">
                            <div class="text-center py-4">
                                <div class="spinner-border text-primary" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `);
        }
    }
}

// Fetch usage data for a specific node
function fetchNodeUsage(nodeId) {
    $.ajax({
        url: `/traffic/latest-per-node/${nodeId}`,
        method: "GET",
        dataType: "json",
        success: function(data) {
            usageCardMap.set(data.nodeId, createUsageCard(data));
            renderSortedUsageCards();
            showToast(`Node ${nodeId} data updated`, 'success');
        },
        error: function() {
            showToast(`Failed to load Node ${nodeId} data`, 'danger');
            usageCardMap.set(nodeId, createFallbackCard(nodeId));
            renderSortedUsageCards();
        }
    });
}

// Initialize by fetching data for all nodes
$(document).ready(function() {
    // First render empty cards
    renderSortedUsageCards();
    
    // Then fetch data for each node
    for (let i = 0; i < 5; i++) {
        fetchNodeUsage(i);
    }
    
    // Optional: Refresh data every 30 seconds
    setInterval(() => {
        for (let i = 0; i < 5; i++) {
            fetchNodeUsage(i);
        }
    }, 30000);
});