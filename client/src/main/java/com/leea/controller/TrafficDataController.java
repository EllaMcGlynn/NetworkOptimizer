package com.leea.controller;

import com.leea.dtos.ResourceUsageStats;
import com.leea.models.TrafficData;
import com.leea.service.TrafficDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/traffic")
public class TrafficDataController {

    @Autowired
    private TrafficDataService trafficDataService;


    @GetMapping
    public ResponseEntity<Page<TrafficData>> getAllTrafficData(@RequestParam int page, @RequestParam int size) {
        Page<TrafficData> trafficData = trafficDataService.getTrafficData(page, size);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrafficData> getTrafficData(@PathVariable Long id) {
        Optional<TrafficData> trafficData = trafficDataService.getTrafficDataById(id);
        return trafficData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByNode(@PathVariable Integer nodeId) {
        List<TrafficData> trafficData = trafficDataService.getTrafficDataByNode(nodeId);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/network/{networkId}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByNetwork(@PathVariable Integer networkId) {
        List<TrafficData> trafficData = trafficDataService.getTrafficDataByNetwork(networkId);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/node/{nodeId}/network/{networkId}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByNodeAndNetwork(
            @PathVariable Integer nodeId,
            @PathVariable Integer networkId) {

        List<TrafficData> trafficData = trafficDataService.getTrafficDataByNodeAndNetwork(nodeId, networkId);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/timerange")
    public ResponseEntity<List<TrafficData>> getTrafficDataInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<TrafficData> trafficData = trafficDataService.getTrafficDataInTimeRange(start, end);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/node/{nodeId}/recent")
    public ResponseEntity<List<TrafficData>> getRecentTrafficDataByNode(
            @PathVariable Integer nodeId,
            @RequestParam(defaultValue = "24") int hours) {

        List<TrafficData> trafficData = trafficDataService.getRecentTrafficDataByNode(nodeId, hours);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/resource-types")
    public ResponseEntity<List<String>> getAvailableResourceTypes() {
        List<String> resourceTypes = trafficDataService.getAvailableResourceTypes();
        return ResponseEntity.ok(resourceTypes);
    }

    @GetMapping("/node/{nodeId}/stats")
    public ResponseEntity<ResourceUsageStats> getResourceStats(@PathVariable Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getResourceUsageStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/node/{nodeId}/stats/cpu")
    public ResponseEntity<ResourceUsageStats> getCpuStats(@PathVariable Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getCpuStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/node/{nodeId}/stats/memory")
    public ResponseEntity<ResourceUsageStats> getMemoryStats(@PathVariable Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getMemoryStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/node/{nodeId}/stats/bandwidth")
    public ResponseEntity<ResourceUsageStats> getBandwidthStats(@PathVariable Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getBandwidthStats(nodeId);
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/node/{nodeId}/stats/{resourceType}")
    public ResponseEntity<ResourceUsageStats> getResourceStatsForType(
            @PathVariable Integer nodeId,
            @PathVariable String resourceType) {

        ResourceUsageStats stats;
        switch (resourceType.toLowerCase()) {
            case "cpu":
                stats = trafficDataService.getCpuStats(nodeId);
                break;
            case "memory":
                stats = trafficDataService.getMemoryStats(nodeId);
                break;
            case "bandwidth":
                stats = trafficDataService.getBandwidthStats(nodeId);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(stats);
    }
}