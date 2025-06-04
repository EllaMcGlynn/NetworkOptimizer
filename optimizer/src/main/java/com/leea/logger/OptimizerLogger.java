package com.leea.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.leea.optimizer.models.TrafficData;

public class OptimizerLogger {
	private static final String LOG_DIR = "/src/main/java/com/leea/subscriber/logging/message-logs/";



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
                data.getTimestamp().toString(),
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
    
    public static void logError(String logEntry) {
    	String filename = LOG_DIR + "message-errors.log";
    	
    	try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write log for error " + logEntry);
            e.printStackTrace();
        }
    }
}
