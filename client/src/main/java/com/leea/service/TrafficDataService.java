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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TrafficDataService {

    private final TrafficDataRepo repository;

    @Autowired
    public TrafficDataService(TrafficDataRepo repository) {
        this.repository = repository;
    }

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
        return repository.findByTimestampBetween(start, end);
    }

    public List<TrafficData> getRecentTrafficDataByNode(Integer nodeId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return repository.findRecentByNodeId(nodeId, since);
    }

    public List<String> getAvailableResourceTypes() {
        return Arrays.asList("CPU", "Memory", "Bandwidth");
    }

    public ResourceUsageStats getResourceUsageStats(Integer nodeId) {
        List<TrafficData> trafficData = repository.findByNodeId(nodeId);
        return calculateStats(trafficData);
    }

    public ResourceUsageStats getCpuStats(Integer nodeId) {
        List<TrafficData> trafficData = repository.findByNodeId(nodeId);
        return calculateCpuStats(trafficData);
    }

    public ResourceUsageStats getMemoryStats(Integer nodeId) {
        List<TrafficData> trafficData = repository.findByNodeId(nodeId);
        return calculateMemoryStats(trafficData);
    }

    public ResourceUsageStats getBandwidthStats(Integer nodeId) {
        List<TrafficData> trafficData = repository.findByNodeId(nodeId);
        return calculateBandwidthStats(trafficData);
    }

    private ResourceUsageStats calculateStats(List<TrafficData> trafficData) {
        if (trafficData.isEmpty()) {
            return new ResourceUsageStats(0, 0, 0, 0, 0, 0);
        }

        double avgCpuUsage = trafficData.stream()
                .mapToDouble(t -> t.getCpuUsage() != null ? t.getCpuUsage() : 0)
                .average().orElse(0);
        double avgMemoryUsage = trafficData.stream()
                .mapToDouble(t -> t.getMemoryUsage() != null ? t.getMemoryUsage() : 0)
                .average().orElse(0);
        double avgBandwidthUsage = trafficData.stream()
                .mapToDouble(t -> t.getBandwidthUsage() != null ? t.getBandwidthUsage() : 0)
                .average().orElse(0);

        double maxCpuUsage = trafficData.stream()
                .mapToDouble(t -> t.getCpuUsage() != null ? t.getCpuUsage() : 0)
                .max().orElse(0);
        double maxMemoryUsage = trafficData.stream()
                .mapToDouble(t -> t.getMemoryUsage() != null ? t.getMemoryUsage() : 0)
                .max().orElse(0);
        double maxBandwidthUsage = trafficData.stream()
                .mapToDouble(t -> t.getBandwidthUsage() != null ? t.getBandwidthUsage() : 0)
                .max().orElse(0);

        return new ResourceUsageStats(avgCpuUsage, avgMemoryUsage, avgBandwidthUsage,
                maxCpuUsage, maxMemoryUsage, maxBandwidthUsage);
    }

    private ResourceUsageStats calculateCpuStats(List<TrafficData> trafficData) {
        if (trafficData.isEmpty()) {
            return new ResourceUsageStats(0, 0, 0, 0, 0, 0);
        }

        double avgCpuUsage = trafficData.stream()
                .mapToDouble(t -> t.getCpuUsage() != null ? t.getCpuUsage() : 0.0)
                .average().orElse(0.0);

        double maxCpuUsage = trafficData.stream()
                .mapToDouble(t -> t.getCpuUsage() != null ? t.getCpuUsage() : 0.0)
                .max().orElse(0.0);

        return new ResourceUsageStats(avgCpuUsage, 0, 0, maxCpuUsage, 0, 0);
    }

    private ResourceUsageStats calculateMemoryStats(List<TrafficData> trafficData) {
        if (trafficData.isEmpty()) {
            return new ResourceUsageStats(0, 0, 0, 0, 0, 0);
        }

        double avgMemoryUsage = trafficData.stream()
                .mapToDouble(t -> t.getMemoryUsage() != null ? t.getMemoryUsage() : 0.0)
                .average().orElse(0.0);

        double maxMemoryUsage = trafficData.stream()
                .mapToDouble(t -> t.getMemoryUsage() != null ? t.getMemoryUsage() : 0.0)
                .max().orElse(0.0);

        return new ResourceUsageStats(0, avgMemoryUsage, 0, 0, maxMemoryUsage, 0);
    }

    private ResourceUsageStats calculateBandwidthStats(List<TrafficData> trafficData) {
        if (trafficData.isEmpty()) {
            return new ResourceUsageStats(0, 0, 0, 0, 0, 0);
        }

        double avgBandwidthUsage = trafficData.stream()
                .mapToDouble(t -> t.getBandwidthUsage() != null ? t.getBandwidthUsage() : 0.0)
                .average().orElse(0.0);

        double maxBandwidthUsage = trafficData.stream()
                .mapToDouble(t -> t.getBandwidthUsage() != null ? t.getBandwidthUsage() : 0.0)
                .max().orElse(0.0);

        return new ResourceUsageStats(0, 0, avgBandwidthUsage, 0, 0, maxBandwidthUsage);
    }

    public Optional<TrafficData> getMostRecentTrafficDataByNode(Integer nodeId) {
        List<TrafficData> results = repository
                .findTopByNodeIdOrderByTimestampDesc(nodeId, PageRequest.of(0, 1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<TrafficData> getRecentUsageForAllNodes() {
        return repository.findTop30ByOrderByTimestampDesc();
    }
}
