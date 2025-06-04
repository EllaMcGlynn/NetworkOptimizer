package com.leea.optimizer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actions")
public class OptimizerActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id")
    private Integer nodeId;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "amount")
    private double amount;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "executed_by")
    private String executedBy;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // --- Getters and Setters ---

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Integer getNodeId() { return nodeId; }

    public void setNodeId(Integer nodeId) { this.nodeId = nodeId; }

    public String getResourceType() { return resourceType; }

    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public double getAmount() { return amount; }

    public void setAmount(double amount) { this.amount = amount; }

    public String getActionType() { return actionType; }

    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getExecutedBy() { return executedBy; }

    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
