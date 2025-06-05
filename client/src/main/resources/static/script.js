// Map to store node cards
const usageCardMap = new Map();

// Toast notification function
function showToast(message, type = 'info') {
  const toastId = `info-toast-${Date.now()}`;
  const bgClass = {
    'success': 'bg-success',
    'danger': 'bg-danger',
    'warning': 'bg-warning',
    'info': 'bg-info'
  }[type] || 'bg-info';

  const toastHtml = `
    <div id="${toastId}" class="toast text-white ${bgClass} border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  `;

  document.getElementById("toastContainer").insertAdjacentHTML("beforeend", toastHtml);
  const toastElement = document.getElementById(toastId);
  const toast = new bootstrap.Toast(toastElement, { delay: 4000 });
  toast.show();

  toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
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

    fetchGroupedUsageHistory();
    setInterval(fetchGroupedUsageHistory, 30000);
    fetchAndGenerateRecommendations();
    setInterval(fetchAndGenerateRecommendations, 30000);

    // Refresh data every 30 seconds
    setInterval(() => {
        for (let i = 0; i < 5; i++) {
            fetchNodeUsage(i);
        }
    }, 30000);
});

//CHARTS
function fetchGroupedUsageHistory() {
  $.ajax({
    url: "/traffic/usage-history/all",
    method: "GET",
    dataType: "json",
    success: function (data) {
      const { timestamps, cpuByNode, memoryByNode, bandwidthByNode } = groupDataByNodeAndTime(data);
      renderMultiLineChart("cpuChart", "CPU Usage (%)", cpuByNode, timestamps);
      renderMultiLineChart("memoryChart", "Memory Usage (GB)", memoryByNode, timestamps);
      renderMultiLineChart("bandwidthChart", "Bandwidth Usage (Mbps)", bandwidthByNode, timestamps);
    },
    error: () => showAlert("Failed to load grouped usage history.")
  });
}

function groupDataByNodeAndTime(data) {
  const timestamps = [...new Set(data.map(d => new Date(d.timestamp).toLocaleTimeString()))].sort();
  const cpuByNode = {};
  const memoryByNode = {};
  const bandwidthByNode = {};

  data.forEach(d => {
    const time = new Date(d.timestamp).toLocaleTimeString();
    const nodeId = d.nodeId;
    if (!cpuByNode[nodeId]) {
      cpuByNode[nodeId] = {};
      memoryByNode[nodeId] = {};
      bandwidthByNode[nodeId] = {};
    }
    cpuByNode[nodeId][time] = d.cpuUsage;
    memoryByNode[nodeId][time] = (d.memoryUsage / 1024).toFixed(2);
    bandwidthByNode[nodeId][time] = d.bandwidthUsage;
  });

  return { timestamps, cpuByNode, memoryByNode, bandwidthByNode };
}

const chartColorMap = ['#e74c3c', '#f39c12', '#3498db', '#9b59b6', '#1abc9c', '#e67e22', '#2ecc71'];

function renderMultiLineChart(canvasId, title, nodeDataMap, timestamps) {
  const ctx = document.getElementById(canvasId).getContext('2d');

  if (ctx.chartInstance) {
    ctx.chartInstance.destroy();
  }

  const datasets = Object.entries(nodeDataMap).map(([nodeId, dataPoints], idx) => {
    const color = chartColorMap[idx % chartColorMap.length];
    return {
      label: `Node ${nodeId}`,
      data: timestamps.map(t => dataPoints[t] ?? null),
      borderColor: color,
      backgroundColor: color, // ← This fills the legend box
      tension: 0.4,
      pointRadius: 3,
      pointHoverRadius: 6,
      borderWidth: 2,
      fill: false
    };
  });

  const chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: timestamps,
      datasets
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          onClick: (e, legendItem, legend) => {
            const i = legendItem.datasetIndex;
            chart.data.datasets.forEach((ds, idx) => {
              chart.setDatasetVisibility(idx, idx === i);
            });
            chart.update();
          }
        },
        tooltip: {
          mode: 'index',
          intersect: false
        }
      },
      interaction: {
        mode: 'nearest',
        axis: 'x',
        intersect: false
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: '#eee'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      }
    }
  });

  ctx.chartInstance = chart;
}

//RECOMMENDATIONS
const previousRecommendations = new Map();
const recommendationLog = [];

let hasRenderedOnce = false;

function fetchAndGenerateRecommendations() {
  $.ajax({
    url: "/traffic/usage-history/all",
    method: "GET",
    dataType: "json",
    success: function (data) {
      const latestByNode = {};

      data.forEach(entry => {
        const key = entry.nodeId;
        if (!latestByNode[key] || new Date(entry.timestamp) > new Date(latestByNode[key].timestamp)) {
          latestByNode[key] = entry;
        }
      });

      processRecommendations(Object.values(latestByNode), !hasRenderedOnce);
      hasRenderedOnce = true;
    },
    error: function () {
      showAlert("Failed to generate optimization recommendations.");
    }
  });
}

function processRecommendations(data, showAll = false) {
  const newRecs = [];

  data.forEach(entry => {
    const { nodeId, cpuUsage, cpuAllocated, memoryUsage, memoryAllocated, bandwidthUsage, bandwidthAllocated, timestamp } = entry;
    const formattedTimestamp = new Date(timestamp).toLocaleString();

    const checks = [
      {
        type: 'CPU',
        usage: `${cpuUsage.toFixed(1)}%`,
        allocation: `${cpuAllocated}%`,
        exceeds: cpuUsage > 0.8 * cpuAllocated,
        recommendation: "Consider increasing CPU allocation"
      },
      {
        type: 'Memory',
        usage: `${(memoryUsage / 1024).toFixed(2)} GB`,
        allocation: `${(memoryAllocated / 1024).toFixed(2)} GB`,
        exceeds: memoryUsage > 0.8 * memoryAllocated,
        recommendation: "Consider increasing memory"
      },
      {
        type: 'Bandwidth',
        usage: `${bandwidthUsage.toFixed(1)} Mbps`,
        allocation: `${bandwidthAllocated} Mbps`,
        exceeds: bandwidthUsage > 0.8 * bandwidthAllocated,
        recommendation: "Consider increasing bandwidth"
      }
    ];

    checks.forEach(check => {
      if (check.exceeds) {
        const id = `${nodeId}-${check.type}-${timestamp}`;

        const recommendationKey = `${nodeId}-${check.type}`;
        const lastSeen = previousRecommendations.get(recommendationKey);

        if (showAll || lastSeen !== timestamp) {
          previousRecommendations.set(recommendationKey, timestamp);

          const rec = {
            timestamp: formattedTimestamp,
            nodeId,
            resource: check.type,
            usage: check.usage,
            allocation: check.allocation,
            recommendation: check.recommendation
          };

          recommendationLog.unshift(rec);
          showRecommendationToast(rec);
          newRecs.push(rec);
        }
      }
    });
  });

  renderRecommendationsTable();
}

function renderRecommendationsTable() {
  const tbody = document.querySelector("#recommendationTable tbody");
  tbody.innerHTML = "";

  recommendationLog.forEach(rec => {
    const row = document.createElement("tr");
    [rec.timestamp, rec.nodeId, rec.resource, rec.usage, rec.allocation, rec.recommendation].forEach(val => {
      const td = document.createElement("td");
      td.textContent = val;
      row.appendChild(td);
    });
    tbody.appendChild(row);
  });
}

function showRecommendationToast(rec) {
  const container = document.getElementById("toastContainer");

  const toastId = `toast-${Date.now()}`;
  const toastHtml = `
    <div id="${toastId}" class="toast align-items-center text-white bg-warning border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">
          ⚠️ <strong>Node ${rec.nodeId}</strong> - <em>${rec.resource}</em> usage high.<br>
          ${rec.recommendation}
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  `;

  container.insertAdjacentHTML("beforeend", toastHtml);

  const toastElement = document.getElementById(toastId);
  const toast = new bootstrap.Toast(toastElement, { delay: 5000 });
  toast.show();

  // Optional: Clean up DOM after hidden
  toastElement.addEventListener('hidden.bs.toast', () => {
    toastElement.remove();
  });
}
