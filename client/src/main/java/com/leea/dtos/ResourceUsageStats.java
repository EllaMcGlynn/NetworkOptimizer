package com.leea.dtos;

public class ResourceUsageStats {
    private double avgCpuUsage;
    private double avgMemoryUsage;
    private double avgBandwidthUsage;
    private double maxCpuUsage;
    private double maxMemoryUsage;
    private double maxBandwidthUsage;

    public ResourceUsageStats(double avgCpuUsage, double avgMemoryUsage, double avgBandwidthUsage,
                              double maxCpuUsage, double maxMemoryUsage, double maxBandwidthUsage) {
        this.avgCpuUsage = avgCpuUsage;
        this.avgMemoryUsage = avgMemoryUsage;
        this.avgBandwidthUsage = avgBandwidthUsage;
        this.maxCpuUsage = maxCpuUsage;
        this.maxMemoryUsage = maxMemoryUsage;
        this.maxBandwidthUsage = maxBandwidthUsage;
    }

    //getters

    public double getAvgCpuUsage() {
        return avgCpuUsage;
    }

    public double getAvgMemoryUsage() {
        return avgMemoryUsage;
    }

    public double getAvgBandwidthUsage() {
        return avgBandwidthUsage;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public double getMaxMemoryUsage() {
        return maxMemoryUsage;
    }

    public double getMaxBandwidthUsage() {
        return maxBandwidthUsage;
    }
}
