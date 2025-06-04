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

    public Integer getNodeId() {
        return nodeId;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public Map<String, Double> getResourceUsage() {
        return resourceUsage;
    }

    public Map<String, Double> getResourceAllocated() {
        return resourceAllocated;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
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
