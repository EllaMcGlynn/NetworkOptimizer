package com.leea.generator.model;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DataGeneratorTest {

    @Test
    void testFieldsAreAssignable() {
        DataGenerator data = new DataGenerator();
        data.nodeId = 1;
        data.networkId = 101;
        data.timestamp = Instant.now();
        data.resourceAllocated = Map.of("cpu", 80.0);
        data.resourceUsage = Map.of("cpu", 42.0);

        assertEquals(1, data.nodeId);
        assertEquals(101, data.networkId);
        assertEquals(80.0, data.resourceAllocated.get("cpu"));
        assertEquals(42.0, data.resourceUsage.get("cpu"));
        assertNotNull(data.timestamp);
    }
}
