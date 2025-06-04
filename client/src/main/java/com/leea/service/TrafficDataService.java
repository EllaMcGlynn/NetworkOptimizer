package com.leea.service;

import com.leea.dtos.ResourceUsageStats;
import com.leea.models.TrafficData;
import com.leea.repo.TrafficDataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TrafficDataService {

    @Autowired
    private TrafficDataRepo repository;

    public List<TrafficData> getAllTrafficData() {
        return repository.findAll();
    }

    public Page<TrafficData> getTrafficData(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllOrderByTimeStampDesc(pageable);
    }

    public Optional<TrafficData> getTrafficDataById(Long id) {
        return repository.findById(id);
    }

    public List<TrafficData> getTrafficDataByNode(Integer nodeId) {
        return repository.findByNodeId(nodeId);
    }

    public List<TrafficData> getTrafficDataByNetwork(Integer networkId) {
        return repository.findByNetworkId(networkId);
    }

    public List<TrafficData> getTrafficDataByNodeAndNetwork(Integer nodeId, Integer networkId) {
        return repository.findByNodeIdAndNetworkId(nodeId, networkId);
    }

    public List<TrafficData> getTrafficDataInTimeRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimeStampBetween(start, end);
    }

    public List<TrafficData> getRecentTrafficDataByNode(Integer nodeId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return repository.findRecentByNodeId(nodeId, since);
    }

    public List<String> getAvailableResourceTypes() {
        return repository.findAllResourceTypes();
    }

    public ResourceUsageStats getResourceUsageStats(Integer nodeId) {
        List<TrafficData> trafficData = repository.findByNodeId(nodeId);
        return calculateStats(trafficData);
    }

    public ResourceUsageStats getResourceStats(Integer nodeId, String resourceType) {
        List<TrafficData> trafficData = repository.findByNodeId(nodeId);
        return calculateStatsForResource(trafficData, resourceType);
    }

    private ResourceUsageStats calculateStats(List<TrafficData> trafficData) {
        if (trafficData.isEmpty()) {
            return new ResourceUsageStats(0,0,0,0,0,0);
        }

        double avgCpuUsage = trafficData.stream()
                .mapToDouble(t -> t.getCpuUsage() !=null ? t.getCpuUsage() : 0)
                .average().orElse(0);
        double avgMemoryUsage = trafficData.stream()
                .mapToDouble(t -> t.getMemoryUsage() !=null ? t.getMemoryUsage() : 0)
                .average().orElse(0);
        double avgBandwidthUsage = trafficData.stream()
                .mapToDouble(t -> t.getBandwidthUsage() !=null ? t.getBandwidthUsage() : 0)
                .average().orElse(0);

        double maxCpuUsage = trafficData.stream()
                .mapToDouble(t -> t.getCpuUsage() !=null ? t.getCpuUsage() : 0)
                .max().orElse(0);
        double maxMemoryUsage = trafficData.stream()
                .mapToDouble(t -> t.getMemoryUsage() !=null ? t.getMemoryUsage() : 0)
                .max().orElse(0);
        double maxBandwidthUsage = trafficData.stream()
                .mapToDouble(t -> t.getBandwidthUsage() !=null ? t.getBandwidthUsage() : 0)
                .max().orElse(0);

        return new ResourceUsageStats(avgCpuUsage, avgMemoryUsage, avgBandwidthUsage,
                maxCpuUsage, maxMemoryUsage, maxBandwidthUsage);
    }

    private ResourceUsageStats calculateStatsForResource(List<TrafficData> trafficData, String resourceType) {
        if (trafficData.isEmpty()) {
            return new ResourceUsageStats(0,0,0,0,0,0);
        }

        double avgUsage = trafficData.stream()
                .filter(t -> t.getResourceUsage() !=null && t.getResourceUsage().containsKey(resourceType))
                .mapToDouble(t -> t.getResourceUsage().get(resourceType))
                .average().orElse(0);
        double maxUsage = trafficData.stream()
                .filter(t -> t.getResourceUsage() !=null && t.getResourceUsage().containsKey(resourceType))
                .mapToDouble(t -> t.getResourceUsage().get(resourceType))
                .max().orElse(0);
        double avgAllocated = trafficData.stream()
                .filter(t -> t.getResourceAllocated() !=null && t.getResourceAllocated().containsKey(resourceType))
                .mapToDouble(t -> t.getResourceAllocated().get(resourceType))
                .average().orElse(0);
        double maxAllocated = trafficData.stream()
                .filter(t -> t.getResourceAllocated() !=null && t.getResourceAllocated().containsKey(resourceType))
                .mapToDouble(t -> t.getResourceAllocated().get(resourceType))
                .max().orElse(0);

        return new ResourceUsageStats(avgUsage, 0, 0, maxUsage, 0, 0);
    }
}
