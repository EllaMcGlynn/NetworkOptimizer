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

    private final TrafficDataService trafficDataService;

    @Autowired
    public TrafficDataController(TrafficDataService trafficDataService) {
        this.trafficDataService = trafficDataService;
    }



    @GetMapping
    public ResponseEntity<Page<TrafficData>> getAllTrafficData(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        Page<TrafficData> trafficData = trafficDataService.getTrafficData(page, size);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrafficData> getTrafficData(@PathVariable("id") Long id) {
        Optional<TrafficData> trafficData = trafficDataService.getTrafficDataById(id);
        return trafficData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByNode(@PathVariable("nodeId") Integer nodeId) {
        List<TrafficData> trafficData = trafficDataService.getTrafficDataByNode(nodeId);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/network/{networkId}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByNetwork(@PathVariable("networkId") Integer networkId) {
        List<TrafficData> trafficData = trafficDataService.getTrafficDataByNetwork(networkId);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/node/{nodeId}/network/{networkId}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByNodeAndNetwork(
            @PathVariable("nodeId") Integer nodeId,
            @PathVariable("networkId") Integer networkId) {

        List<TrafficData> trafficData = trafficDataService.getTrafficDataByNodeAndNetwork(nodeId, networkId);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/timerange")
    public ResponseEntity<List<TrafficData>> getTrafficDataInTimeRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<TrafficData> trafficData = trafficDataService.getTrafficDataInTimeRange(start, end);
        return ResponseEntity.ok(trafficData);
    }

    @GetMapping("/node/{nodeId}/recent")
    public ResponseEntity<List<TrafficData>> getRecentTrafficDataByNode(
            @PathVariable("nodeId") Integer nodeId,
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
    public ResponseEntity<ResourceUsageStats> getResourceStats(@PathVariable("nodeId") Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getResourceUsageStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/node/{nodeId}/stats/cpu")
    public ResponseEntity<ResourceUsageStats> getCpuStats(@PathVariable("nodeId") Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getCpuStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/node/{nodeId}/stats/memory")
    public ResponseEntity<ResourceUsageStats> getMemoryStats(@PathVariable("nodeId") Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getMemoryStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/node/{nodeId}/stats/bandwidth")
    public ResponseEntity<ResourceUsageStats> getBandwidthStats(@PathVariable("nodeId") Integer nodeId) {
        ResourceUsageStats stats = trafficDataService.getBandwidthStats(nodeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/latest-per-node/{nodeId}")
    public ResponseEntity<TrafficData> getLatestTrafficDataForNode(@PathVariable("nodeId") Integer nodeId) {
        Optional<TrafficData> data = trafficDataService.getMostRecentTrafficDataByNode(nodeId);
        return data.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usage-history/all")
    public ResponseEntity<List<TrafficData>> getAllRecentUsageHistory() {
        List<TrafficData> history = trafficDataService.getRecentUsageForAllNodes();
        return ResponseEntity.ok(history);
    }


}