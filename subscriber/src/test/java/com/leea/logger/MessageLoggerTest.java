package com.leea.logger;

import com.leea.models.TrafficData;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessageLoggerTest {

    private static final String LOG_DIR = "subscriber/src/main/java/com/leea/subscriber/logging/message-logs/";
    private static final String ERROR_LOG_FILE = LOG_DIR + "message-errors.log";
    private String testLogFile;

    private TrafficData sampleData;

    @BeforeEach
    void setup() throws IOException {
        sampleData = new TrafficData();
        sampleData.setNodeId(101);
        sampleData.setNetworkId(202);

        Map<String, Double> usage = new HashMap<>();
        usage.put("CPU", 72.5);
        usage.put("Memory", 58.2);
        sampleData.setResourceUsage(usage);

        Map<String, Double> allocated = new HashMap<>();
        allocated.put("CPU", 100.0);
        allocated.put("Memory", 100.0);
        sampleData.setResourceAllocated(allocated);

        LocalDateTime timestamp = LocalDateTime.of(2025, 6, 6, 12, 0);
        double epochSeconds = timestamp.toEpochSecond(java.time.ZoneOffset.UTC);
        sampleData.setTimestamp(epochSeconds);


        testLogFile = LOG_DIR + "message-" + sampleData.getNodeId() + ".log";

        Files.deleteIfExists(Paths.get(testLogFile));
        Files.deleteIfExists(Paths.get(ERROR_LOG_FILE));
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(testLogFile));
        Files.deleteIfExists(Paths.get(ERROR_LOG_FILE));
    }

    @Test
    void testLog_createsCorrectLogEntry() throws IOException {
        MessageLogger.log(sampleData);

        List<String> lines = Files.readAllLines(Paths.get(testLogFile));
        assertEquals(1, lines.size());

        String line = lines.get(0);
        assertTrue(line.contains("NodeID: 101"));
        assertTrue(line.contains("NetworkID: 202"));
        assertTrue(line.contains("CPU=72.5"));
        assertTrue(line.contains("Memory=58.2"));
        assertTrue(line.contains("CPU=100.0"));
        assertTrue(line.contains("Memory=100.0"));
        assertTrue(line.contains(sampleData.getTimestamp().toString()));
    }

    @Test
    void testLogError_writesToErrorFile() throws IOException {
        String errorMsg = "[2025-06-06T12:00:00] Failed to connect to node\n";
        MessageLogger.logError(errorMsg);

        List<String> lines = Files.readAllLines(Paths.get(ERROR_LOG_FILE));
        assertEquals(1, lines.size());
        assertEquals(errorMsg.strip(), lines.get(0).strip());
    }
}
