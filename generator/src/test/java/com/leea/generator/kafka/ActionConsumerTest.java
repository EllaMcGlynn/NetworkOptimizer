package com.leea.generator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.generator.model.OptimizerAction;
import com.leea.generator.service.DataGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ActionConsumerTest {

    private DataGeneratorService dataGeneratorService;
    private ActionConsumer actionConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dataGeneratorService = mock(DataGeneratorService.class);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // This registers JavaTimeModule
        actionConsumer = new ActionConsumer(dataGeneratorService, objectMapper);
    }

    @Test
    void testConsumeCallsApplyOptimizerAction() throws Exception {
        // Create test action
        OptimizerAction action = new OptimizerAction();
        action.setNodeId(1);
        action.setResourceType("memory");
        action.setActionType("INCREASE");
        action.setAmount(512.0);
        action.setExecutedBy("OPTIMIZER");
        action.setTimestamp(LocalDateTime.now());

        // Convert action to JSON string
        String actionJson = objectMapper.writeValueAsString(action);

        // Call the consume method with JSON string
        actionConsumer.consume(actionJson);

        // Verify that the service method is called with the correct action
        ArgumentCaptor<OptimizerAction> captor = ArgumentCaptor.forClass(OptimizerAction.class);
        verify(dataGeneratorService, times(1)).applyOptimizerAction(captor.capture());

        OptimizerAction capturedAction = captor.getValue();
        assertEquals(action.getNodeId(), capturedAction.getNodeId());
        assertEquals(action.getResourceType(), capturedAction.getResourceType());
        assertEquals(action.getActionType(), capturedAction.getActionType());
        assertEquals(action.getAmount(), capturedAction.getAmount(), 0.0001);
        assertEquals(action.getExecutedBy(), capturedAction.getExecutedBy());
    }
}