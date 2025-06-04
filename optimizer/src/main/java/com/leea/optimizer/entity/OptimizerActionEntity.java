package com.leea.optimizer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actions")
public class OptimizerActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer nodeId;
    private String resourceType;
    private double amount;
    private String actionType; // INCREASE, DECREASE
    private String executedBy; // USER or OPTIMIZER
    private LocalDateTime timestamp;

    public void setNodeId(Object nodeId) {
    }

    public void setResourceType(Object resourceType) {
        
    }

    public void setAmount(Object amount) {
    }

    public void setActionType(Object actionType) {
    }

    public void setExecutedBy(Object executedBy) {
    }

    public void setTimestamp(Object timestamp) {

    }
}
