package com.leea.generator.service;

import com.leea.generator.kafka.DataGeneratorProducer;
import com.leea.generator.model.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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

    @Test
    void testGenAndSendData_sendsDataWithExpectedFields() {
        service.genAndSendData();

        ArgumentCaptor<DataGenerator> captor = ArgumentCaptor.forClass(DataGenerator.class);
        verify(producer, times(5)).send(captor.capture());

        for (DataGenerator data : captor.getAllValues()) {
            assertNotNull(data);
            assertTrue(data.nodeId >= 0 && data.nodeId < 5);
            assertEquals(100 + data.nodeId, data.networkId);
            assertNotNull(data.resourceAllocated);
            assertNotNull(data.resourceUsage);
            assertNotNull(data.timestamp);

            for (String key : data.resourceAllocated.keySet()) {
                double allocated = data.resourceAllocated.get(key);
                double used = data.resourceUsage.get(key);
                assertTrue(used >= allocated * 0.5 && used <= allocated);
            }
        }
    }
}
