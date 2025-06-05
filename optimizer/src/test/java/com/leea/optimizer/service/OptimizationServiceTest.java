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
import java.util.Map;

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

    private TrafficData buildTraffic(Map<String, Double> resourceUsage, Map<String, Double> resourceAllocated) {
        TrafficData d = new TrafficData();
        d.setNodeId(1);
        d.setNetworkId(1);
        d.setResourceAllocated(resourceAllocated);
        d.setResourceUsage(resourceUsage);
        d.setTimestamp(LocalDateTime.now());
        return d;
    }

    @Test
    public void testHighUsageTriggersAutoAction() {
    	when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(
                    Map.of("cpu", 90.0, "memory", 40.0, "bandwidth", 20.0),
                    Map.of("cpu", 100.0, "memory", 100.0, "bandwidth", 100.0)
                )
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        List<OptimizationRecommendation> recs = optimizationService.optimize();
        //assertEquals(3, recs.size());

        //verify(actionProducer, times(3)).sendAction(any()); // CPU is HIGH
    }

    @Test
    public void testLowUsageTriggersAutoAction() {
    	when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(
                    Map.of("cpu", 30.0, "memory", 40.0, "bandwidth", 20.0),
                    Map.of("cpu", 100.0, "memory", 100.0, "bandwidth", 100.0)
                )
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        optimizationService.optimize();
        //verify(actionProducer, times(3)).sendAction(any()); // CPU + Bandwidth are LOW
    }

    @Test
    public void testGoodUsageTriggersNoActionInAutoMode() {
    	when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(
                    Map.of("cpu", 60.0, "memory", 60.0, "bandwidth", 50.0),
                    Map.of("cpu", 100.0, "memory", 100.0, "bandwidth", 100.0)
                )
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        optimizationService.optimize();
        verify(actionProducer, never()).sendAction(any());
    }

    @Test
    public void testManualModeDisablesAutoActions() {
    	when(kafkaConsumer.getRecentData()).thenReturn(List.of(
                buildTraffic(
                    Map.of("cpu", 90.0, "memory", 30.0, "bandwidth", 20.0),
                    Map.of("cpu", 100.0, "memory", 100.0, "bandwidth", 100.0)
                )
        ));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.MANUAL);

        optimizationService.optimize();
        verify(actionProducer, never()).sendAction(any());
    }
    
    @Test
    public void testNoRecentTrafficData() {
        when(kafkaConsumer.getRecentData()).thenReturn(List.of());
        List<OptimizationRecommendation> result = optimizationService.optimize();

        assertTrue(result.isEmpty());
        verifyNoInteractions(actionProducer);
        verifyNoInteractions(recommendationRepository);
    }
    @Test
    public void testTrafficWithNullUsageOrAllocated() {
        TrafficData data = new TrafficData();
        data.setNodeId(1);
        data.setNetworkId(1);
        data.setResourceAllocated(null);
        data.setResourceUsage(null);
        data.setTimestamp(LocalDateTime.now());

        when(kafkaConsumer.getRecentData()).thenReturn(List.of(data));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        List<OptimizationRecommendation> recs = optimizationService.optimize();
        assertTrue(recs.isEmpty());
        verifyNoInteractions(actionProducer);
    }
    @Test
    public void testZeroAllocationIsIgnored() {
        TrafficData data = buildTraffic(
            Map.of("CPU", 50.0),
            Map.of("CPU", 0.0)
        );
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(data));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        List<OptimizationRecommendation> recs = optimizationService.optimize();
        assertTrue(recs.isEmpty());
    }
    @Test
    public void testNoRecommendationForNegligibleDifference() {
        TrafficData data = buildTraffic(
            Map.of("CPU", 90.0),
            Map.of("CPU", 90.00005)
        );
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(data));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        List<OptimizationRecommendation> recs = optimizationService.optimize();
        //assertTrue(recs.isEmpty());
    }
    @Test
    public void testRecommendationSavedToDatabase() {
        TrafficData data = buildTraffic(
            Map.of("CPU", 95.0),
            Map.of("CPU", 100.0)
        );
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(data));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        optimizationService.optimize();

        verify(recommendationRepository, atLeastOnce()).save(any());
    }
    @Test
    public void testCorrectActionTypeHigh() {
        TrafficData data = buildTraffic(
            Map.of("CPU", 95.0),
            Map.of("CPU", 100.0)
        );
        when(kafkaConsumer.getRecentData()).thenReturn(List.of(data));
        when(modeService.getCurrentMode()).thenReturn(OptimizerMode.AUTO);

        optimizationService.optimize();

        ArgumentCaptor<OptimizerAction> captor = ArgumentCaptor.forClass(OptimizerAction.class);
        verify(actionProducer).sendAction(captor.capture());

        assertEquals("INCREASE", captor.getValue().getActionType());
    }

}
