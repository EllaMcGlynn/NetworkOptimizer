package com.leea.optimizer.models;

import java.time.LocalDateTime;

public class OptimizerAction {
    private Integer nodeId;
    private String resourceType;
    private double amount;
    private String actionType; // INCREASE, DECREASE
    private String executedBy; // USER or OPTIMIZER
    private LocalDateTime timestamp;
}
