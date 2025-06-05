package com.leea.optimizer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class OptimizationRecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer nodeId;
    private Integer networkId;
    private String resourceType;
    private double currentUsage;
    private double currentAllocation;
    private double recommendedAllocation;
    private LocalDateTime timestamp;

    public void setNodeId(Integer nodeId) {
    }

    public void setNetworkId(Integer networkId) {
    }

    public void setResourceType(String resourceType) {
    }

    public void setCurrentUsage(double currentUsage) {
    }

    public void setCurrentAllocation(double currentAllocation) {
    }

    public void setRecommendedAllocation(double recommendedAllocation) {
    }

    public void setTimestamp(LocalDateTime timestamp) {

    }
}
