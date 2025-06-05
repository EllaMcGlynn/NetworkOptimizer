package com.leea.generator.service;

import com.leea.generator.kafka.DataGeneratorProducer;
import com.leea.generator.model.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.leea.generator.model.OptimizerAction;
import java.time.LocalDateTime;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataGeneratorServiceTest {

    private DataGeneratorProducer producer;
    private DataGeneratorService service;

    @BeforeEach
    void setup() {
        producer = mock(DataGeneratorProducer.class);
        service = new DataGeneratorService(producer);
    }

    //    @Test
//    void testGenAndSendData_sendsDataWithExpectedFields() {
//        service.genAndSendData();
//
//        ArgumentCaptor<DataGenerator> captor = ArgumentCaptor.forClass(DataGenerator.class);
//        verify(producer, times(5)).send(captor.capture());
//
//        for (DataGenerator data : captor.getAllValues()) {
//            assertNotNull(data);
//            assertTrue(data.nodeId >= 0 && data.nodeId < 5);
//            assertEquals(100 + data.nodeId, data.networkId);
//            assertNotNull(data.resourceAllocated);
//            assertNotNull(data.resourceUsage);
//            assertNotNull(data.timestamp);
//
//            for (String key : data.resourceAllocated.keySet()) {
//                double allocated = data.resourceAllocated.get(key);
//                double used = data.resourceUsage.get(key);
//                assertTrue(used >= allocated * 0.5 && used <= allocated);
//            }
//        }
//    }
    @Test
    void testApplyOptimizerAction_increaseAllocation() {
        int nodeId = 0;
        String resource = "cpu";
        double original = 80.0;
        double increase = 10.0;

        OptimizerAction action = new OptimizerAction();
        action.setNodeId(nodeId);
        action.setResourceType(resource);
        action.setActionType("INCREASE");
        action.setAmount(increase);
        action.setExecutedBy("OPTIMIZER");
        action.setTimestamp(LocalDateTime.now());

        service.applyOptimizerAction(action);

        // Generate data and verify allocation was updated
        service.genAndSendData();

        ArgumentCaptor<DataGenerator> captor = ArgumentCaptor.forClass(DataGenerator.class);
        verify(producer, atLeastOnce()).send(captor.capture());

        // Find the matching node
        DataGenerator updatedNode = captor.getAllValues().stream()
                .filter(data -> data.nodeId == nodeId)
                .findFirst()
                .orElseThrow();

        double newAlloc = updatedNode.resourceAllocated.get(resource);
        assertEquals(original + increase, newAlloc, 0.01);
    }

    @Test
    void testApplyOptimizerAction_decreaseAllocation() {
        int nodeId = 1;
        String resource = "memory";
        double original = 2048.0;
        double decrease = 512.0;

        OptimizerAction action = new OptimizerAction();
        action.setNodeId(nodeId);
        action.setResourceType(resource);
        action.setActionType("DECREASE");
        action.setAmount(decrease);
        action.setExecutedBy("USER");
        action.setTimestamp(LocalDateTime.now());

        service.applyOptimizerAction(action);
        service.genAndSendData();

        ArgumentCaptor<DataGenerator> captor = ArgumentCaptor.forClass(DataGenerator.class);
        verify(producer, atLeastOnce()).send(captor.capture());

        DataGenerator updatedNode = captor.getAllValues().stream()
                .filter(data -> data.nodeId == nodeId)
                .findFirst()
                .orElseThrow();

        double newAlloc = updatedNode.resourceAllocated.get(resource);
        assertEquals(original - decrease, newAlloc, 0.01);
    }

    @Test
    void testApplyOptimizerAction_preventsNegativeAllocation() {
        int nodeId = 2;
        String resource = "bandwith";
        double original = 500.0;
        double excessiveDecrease = 9999.0;

        OptimizerAction action = new OptimizerAction();
        action.setNodeId(nodeId);
        action.setResourceType(resource);
        action.setActionType("DECREASE");
        action.setAmount(excessiveDecrease);
        action.setExecutedBy("OPTIMIZER");
        action.setTimestamp(LocalDateTime.now());

        service.applyOptimizerAction(action);
        service.genAndSendData();

        ArgumentCaptor<DataGenerator> captor = ArgumentCaptor.forClass(DataGenerator.class);
        verify(producer, atLeastOnce()).send(captor.capture());

        DataGenerator updatedNode = captor.getAllValues().stream()
                .filter(data -> data.nodeId == nodeId)
                .findFirst()
                .orElseThrow();

        double newAlloc = updatedNode.resourceAllocated.get(resource);
        assertEquals(0.0, newAlloc, 0.01);
    }
}
