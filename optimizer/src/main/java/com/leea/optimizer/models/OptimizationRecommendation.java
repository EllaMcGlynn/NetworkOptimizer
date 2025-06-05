package com.leea.optimizer.models;

import java.time.LocalDateTime;

public class OptimizationRecommendation {
    private Integer nodeId;
    private Integer networkId;
    private String resourceType;
    private double currentUsage;
    private double currentAllocation;
    private double recommendedAllocation;
    private LocalDateTime timestamp;

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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public double getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(double currentUsage) {
        this.currentUsage = currentUsage;
    }

    public double getCurrentAllocation() {
        return currentAllocation;
    }

    public void setCurrentAllocation(double currentAllocation) {
        this.currentAllocation = currentAllocation;
    }

    public double getRecommendedAllocation() {
        return recommendedAllocation;
    }

    public void setRecommendedAllocation(double recommendedAllocation) {
        this.recommendedAllocation = recommendedAllocation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
