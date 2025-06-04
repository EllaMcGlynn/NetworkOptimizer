package com.leea.optimizer.service;

import com.leea.optimizer.kafka.ActionKafkaProducer;
import com.leea.optimizer.models.*;
import com.leea.optimizer.repository.ActionRepository;
import com.leea.optimizer.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OptimizationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private OptimizationService optimizationService;

    @Mock
    private KafkaTrafficDataConsumer kafkaConsumer;

    @Mock
    private OptimizerModeService modeService;

    @Mock
    private ActionKafkaProducer actionProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TrafficData buildTraffic(double cpuUsage, double memUsage, double bwUsage,
                                     double cpuAllocated, double memAllocated, double bwAllocated) {
        TrafficData d = new TrafficData();
        d.setNodeId(1);
        d.setNetworkId(1);
        d.setCpuUsage(cpuUsage);
        d.setCpuAllocated(cpuAllocated);
        d.setMemoryUsage(memUsage);
        d.setMemoryAllocated(memAllocated);
        d.setBandwidthUsage(bwUsage);
        d.setBandwidthAllocated(bwAllocated);
        d.setTimestamp(LocalDateTime.now());
        return d;
    }

    @Test
    public void testHighUsageTriggersAutoAction() {
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(90, 40, 20, 100, 100, 100)
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        List<OptimizationRecommendation> recs = optimizationService.optimize();
        assertEquals(3, recs.size());

        verify(actionProducer, times(3)).sendAction(any()); // CPU is HIGH
    }

    @Test
    public void testLowUsageTriggersAutoAction() {
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(30, 40, 20, 100, 100, 100)
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        optimizationService.optimize();
        verify(actionProducer, times(3)).sendAction(any()); // CPU + Bandwidth are LOW
    }

    @Test
    public void testGoodUsageTriggersNoActionInAutoMode() {
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(60, 60, 50, 100, 100, 100)
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        optimizationService.optimize();
        verify(actionProducer, never()).sendAction(any());
    }

    @Test
    public void testManualModeDisablesAutoActions() {
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(90, 30, 20, 100, 100, 100)
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.MANUAL);

        optimizationService.optimize();
        verify(actionProducer, never()).sendAction(any());
    }
}
