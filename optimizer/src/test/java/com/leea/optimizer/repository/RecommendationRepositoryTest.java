package com.leea.optimizer.repository;

import com.leea.optimizer.entity.OptimizationRecommendationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RecommendationRepositoryTest {
    @Autowired
    private RecommendationRepository repository;

    @Test
    public void testSave() {
        OptimizationRecommendationEntity entity = new OptimizationRecommendationEntity();
        entity.setNodeId(1);
        entity.setNetworkId(100);
        entity.setResourceType("CPU");
        entity.setCurrentUsage(75.5);
        entity.setCurrentAllocation(100.0);
        entity.setRecommendedAllocation(110.0);
        entity.setTimestamp(LocalDateTime.now());

        repository.save(entity);
        List<OptimizationRecommendationEntity> results = repository.findAll();

        assertFalse(results.isEmpty());
    }
}
