package com.leea.optimizer.repository;

import com.leea.optimizer.entity.OptimizationRecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<OptimizationRecommendationEntity, Long> {}
