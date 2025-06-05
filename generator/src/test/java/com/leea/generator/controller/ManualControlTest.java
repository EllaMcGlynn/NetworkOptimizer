package com.leea.generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leea.generator.model.OptimizerAction;
import com.leea.generator.service.DataGeneratorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManualControl.class)
class ManualControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataGeneratorService dataGen;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSetModeEndpoint() throws Exception {
        OptimizerAction action = new OptimizerAction();
        action.setNodeId(1);
        action.setResourceType("cpu");
        action.setAmount(10.0);
        action.setActionType("INCREASE");
        action.setExecutedBy("USER");
        action.setTimestamp(LocalDateTime.now());

        mockMvc.perform(post("/api/generator/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isOk());

        ArgumentCaptor<OptimizerAction> captor = ArgumentCaptor.forClass(OptimizerAction.class);
        verify(dataGen, times(1)).applyOptimizerAction(captor.capture());

        OptimizerAction actual = captor.getValue();
        assertEquals(action.getNodeId(), actual.getNodeId());
        assertEquals(action.getResourceType(), actual.getResourceType());
        assertEquals(action.getAmount(), actual.getAmount());
        assertEquals(action.getActionType(), actual.getActionType());
        assertEquals(action.getExecutedBy(), actual.getExecutedBy());
    }

}
