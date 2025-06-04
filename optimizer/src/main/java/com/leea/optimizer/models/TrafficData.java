package com.leea.optimizer.models;

import java.time.LocalDateTime;

public class TrafficData {
    private Integer nodeId;
    private Integer networkId;
    private double cpuUsage;
    private double cpuAllocated;
    private double memoryUsage;
    private double memoryAllocated;
    private double bandwidthUsage;
    private double bandwidthAllocated;
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

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getCpuAllocated() {
        return cpuAllocated;
    }

    public void setCpuAllocated(double cpuAllocated) {
        this.cpuAllocated = cpuAllocated;
    }

    public double getMemoryAllocated() {
        return memoryAllocated;
    }

    public void setMemoryAllocated(double memoryAllocated) {
        this.memoryAllocated = memoryAllocated;
    }

    public double getBandwidthUsage() {
        return bandwidthUsage;
    }

    public void setBandwidthUsage(double bandwidthUsage) {
        this.bandwidthUsage = bandwidthUsage;
    }

    public double getBandwidthAllocated() {
        return bandwidthAllocated;
    }

    public void setBandwidthAllocated(double bandwidthAllocated) {
        this.bandwidthAllocated = bandwidthAllocated;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
