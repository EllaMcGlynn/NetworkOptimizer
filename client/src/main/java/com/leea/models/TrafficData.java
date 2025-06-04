package com.leea.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "traffic_data")
public class TrafficData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id")
    private Integer nodeId;

    @Column(name = "network_id")
    private Integer networkId;

    @ElementCollection
    @CollectionTable(name = "traffic_resource_usage", joinColumns = @JoinColumn(name = "traffic_data_id"))
    @MapKeyColumn(name = "resource_type")
    @Column(name = "value")
    private Map<String, Double> resourceUsage;

    @ElementCollection
    @CollectionTable(name = "traffic_resource_allocated", joinColumns = @JoinColumn(name = "traffic_data_id"))
    @MapKeyColumn(name = "resource_type")
    @Column(name = "value")
    private Map<String, Double> resourceAllocated;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    //Constructors
    public TrafficData() {}

    public TrafficData(Integer nodeId, Integer networkId, Map<String, Double> resourceUsage,
                       Map<String, Double> resourceAllocated, LocalDateTime timestamp) {
        this.nodeId = nodeId;
        this.networkId = networkId;
        this.resourceUsage = resourceUsage;
        this.resourceAllocated = resourceAllocated;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public Map<String, Double> getResourceUsage() {
        return resourceUsage;
    }

    public void setResourceUsage(Map<String, Double> resourceUsage) {
        this.resourceUsage = resourceUsage;
    }

    public Map<String, Double> getResourceAllocated() {
        return resourceAllocated;
    }

    public void setResourceAllocated(Map<String, Double> resourceAllocated) {
        this.resourceAllocated = resourceAllocated;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    //Methods for resource types (could be helpful later if we do something extra to show-off)
    public Double getCpuUsage() {
        return resourceUsage !=null ? resourceUsage.get("cpu") : null;
    }

    public Double getMemoryUsage() {
        return resourceUsage !=null ? resourceUsage.get("memory") : null;
    }

    public Double getBandwidthUsage() {
        return resourceUsage !=null ? resourceUsage.get("bandwidth") : null;
    }

    public Double getCpuAllocated() {
        return resourceAllocated !=null ? resourceAllocated.get("cpu") : null;
    }

    public Double getMemoryAllocated() {
        return resourceAllocated !=null ? resourceAllocated.get("memory") : null;
    }

    public Double getBandwidthAllocated() {
        return resourceAllocated !=null ? resourceAllocated.get("bandwidth") : null;
    }
}
