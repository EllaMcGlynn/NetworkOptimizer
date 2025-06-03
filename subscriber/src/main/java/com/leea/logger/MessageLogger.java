package com.leea.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.leea.models.TrafficData;

public class MessageLogger {
	private static final String LOG_DIR = "generator/src/main/java/com/leea/subscriber/logging/message-logs/";



    static {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create log directory", e);
        }
    }

    public static void log(TrafficData data) {
        String filename = LOG_DIR + "message-" + data.getNodeId() + ".log";

        String logEntry = String.format(
                "[%s] NodeID: %d | NetworkID: %d | Usage: %s | Allocation: %s%n",
                data.getTimeStamp().toString(),
                data.getNodeId(),
                data.getNetworkId(),
                data.getResourceUsage().toString(),
                data.getResourceAllocated().toString()
        );

        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write log for node " + data.getNodeId());
            e.printStackTrace();
        }
    }
}
