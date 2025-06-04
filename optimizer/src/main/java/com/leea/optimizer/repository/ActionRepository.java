package com.leea.optimizer.repository;

import com.leea.optimizer.entity.OptimizerActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<OptimizerActionEntity, Long> {}
