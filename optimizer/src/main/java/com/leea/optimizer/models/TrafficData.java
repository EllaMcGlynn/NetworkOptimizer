package com.leea.optimizer.models;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.leea.optimizer.kafka.UnixTimestampDeserializer;

import lombok.Data;


@Data
public class TrafficData {

    private Integer nodeId;
    private Integer networkId;

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

    private Map<String, Double> resourceUsage;
    private Map<String, Double> resourceAllocated;

    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private LocalDateTime timestamp;




    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
