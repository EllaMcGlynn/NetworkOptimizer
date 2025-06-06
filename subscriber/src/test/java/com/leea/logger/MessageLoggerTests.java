package com.leea.logger;

import com.leea.models.TrafficData;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessageLoggerTest {

    private static final Path LOG_DIR = Paths.get("subscriber/src/main/java/com/leea/subscriber/logging/message-logs/");

    private static final Path ERROR_LOG = LOG_DIR.resolve("message-errors.log");
    private static final Path NODE_LOG = LOG_DIR.resolve("message-999.log");

    @BeforeEach
    void cleanUpBefore() throws IOException {
        Files.createDirectories(LOG_DIR);
        Files.deleteIfExists(NODE_LOG);
        Files.deleteIfExists(ERROR_LOG);
    }

    @AfterEach
    void cleanUpAfter() throws IOException {
        Files.deleteIfExists(NODE_LOG);
        Files.deleteIfExists(ERROR_LOG);
    }

    @Test
    void testLog_createsNodeLogFile() throws IOException {
        TrafficData data = new TrafficData();
        data.setNodeId(999);
        data.setNetworkId(123);
        data.setTimestamp(1717675200.0);
        data.setResourceUsage(Map.of("CPU", 80.0, "RAM", 60.0));
        data.setResourceAllocated(Map.of("CPU", 100.0, "RAM", 100.0));

        MessageLogger.log(data);

        assertTrue(Files.exists(NODE_LOG), "Expected node log file to exist");

        String contents = Files.readString(NODE_LOG);
        assertTrue(contents.contains("NodeID: 999"), "Log entry should contain NodeID");
        assertTrue(contents.contains("Usage: {CPU=80.0, RAM=60.0}"), "Log entry should contain usage map");
    }

    @Test
    void testLogError_createsErrorLogFile() throws IOException {
        String errorMessage = "Something went wrong with node 888\n";
        MessageLogger.logError(errorMessage);

        assertTrue(Files.exists(ERROR_LOG), "Expected error log file to exist");

        String contents = Files.readString(ERROR_LOG);
        assertTrue(contents.contains("Something went wrong with node 888"), "Error log should contain the message");
    }
}
