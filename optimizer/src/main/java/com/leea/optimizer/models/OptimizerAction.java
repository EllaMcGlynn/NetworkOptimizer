package com.leea.optimizer.models;

import java.time.LocalDateTime;

public class OptimizerAction {
    private Integer nodeId;
    private String resourceType;
    private double amount;
    private String actionType; // INCREASE, DECREASE
    private String executedBy; // USER or OPTIMIZER
    private LocalDateTime timestamp;

    public void setNodeId(Integer nodeId) {
    }

    public void setResourceType(String resourceType) {
    }

    public void setAmount(double change) {
    }

    public void setActionType(String actionType) {
    }

    public void setExecutedBy(String optimizer) {
    }

    public void setTimestamp(LocalDateTime now) {
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public double getAmount() {
        return amount;
    }

    public String getActionType() {
        return actionType;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
