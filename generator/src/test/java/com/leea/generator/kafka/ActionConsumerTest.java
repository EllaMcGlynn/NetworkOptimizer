package com.leea.generator.kafka;

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

    @BeforeEach
    void setUp() {
        dataGeneratorService = mock(DataGeneratorService.class);
        actionConsumer = new ActionConsumer(dataGeneratorService);
    }

    @Test
    void testConsumeCallsApplyOptimizerAction() {
        OptimizerAction action = new OptimizerAction();
        action.setNodeId(1);
        action.setResourceType("memory");
        action.setActionType("INCREASE");
        action.setAmount(512.0);
        action.setExecutedBy("OPTIMIZER");
        action.setTimestamp(LocalDateTime.now());

        // Simulate consumer method call.
        //actionConsumer.consume(action);

        // Verify that the service method is called with the same action.
        ArgumentCaptor<OptimizerAction> captor = ArgumentCaptor.forClass(OptimizerAction.class);
        verify(dataGeneratorService, times(1)).applyOptimizerAction(captor.capture());

        OptimizerAction capturedAction = captor.getValue();
        assertEquals(action.getNodeId(), capturedAction.getNodeId());
        assertEquals(action.getResourceType(), capturedAction.getResourceType());
        assertEquals(action.getActionType(), capturedAction.getActionType());
        assertEquals(action.getAmount(), capturedAction.getAmount(), 0.0001);
    }
}
