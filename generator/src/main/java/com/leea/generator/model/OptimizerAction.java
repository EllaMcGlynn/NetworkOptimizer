package com.leea.generator.model;

import java.time.LocalDateTime;

public class OptimizerAction {
    private Integer nodeId;
    private String resourceType;
    private double amount;
    private String actionType;
    private String executedBy;
    private LocalDateTime timestamp;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OptimizerAction{" +
                "nodeId=" + nodeId +
                ", resourceType='" + resourceType + '\'' +
                ", amount=" + amount +
                ", actionType='" + actionType + '\'' +
                ", executedBy='" + executedBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
