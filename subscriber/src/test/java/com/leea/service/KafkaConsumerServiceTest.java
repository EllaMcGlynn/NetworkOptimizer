package com.leea.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leea.SubscriberApplication;
import com.leea.models.TrafficData;
import com.leea.repo.DataRepo;

@SpringBootTest(classes = SubscriberApplication.class)
@AutoConfigureMockMvc
class KafkaConsumerServiceTest {

	@Autowired
    private KafkaConsumerService kafkaConsumerService;

    @MockBean
    private DataRepo repository;

    @Test
    public void testKafkaMessageProcessing() throws JsonProcessingException {
    	String jsonMessage = """
            {
                "nodeID": 1,
                "networkID": 1,
                "resourceUsage": { "cpu": 50.0, "memory": 50.5 },
                "resourceAllocated": { "cpu": 70.0, "memory": 30.5 },
                "timeStamp": "2025-05-30T14:30:00"
            }
        """;
        kafkaConsumerService.listen(jsonMessage);

        ArgumentCaptor<TrafficData> captor = ArgumentCaptor.forClass(TrafficData.class);
        verify(repository, times(1)).save(captor.capture());
        
        TrafficData saved = captor.getValue();
        assertEquals(1, saved.getNodeID());
        assertEquals(1, saved.getNetworkID());
        assertEquals(50.0, saved.getResourceUsage().get("cpu"));
        assertEquals(50.5, saved.getResourceUsage().get("memory"));
        assertEquals(70.0, saved.getResourceAllocated().get("cpu"));
        assertEquals(30.5, saved.getResourceAllocated().get("memory"));
    }
}
