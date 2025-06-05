package com.leea.generator.logging;

import com.leea.generator.model.DataGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class NodeLogger {
    private static final String LOG_DIR = "logging/node-logs";



    static {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create log directory", e);
        }
    }

    public static void log(DataGenerator data) {
        String filename = LOG_DIR + "node-" + data.nodeId + ".log";

        String logEntry = String.format(
                "[%s] NodeID: %d | NetworkID: %d | Usage: %s | Allocation: %s%n",
                data.timestamp.toString(),
                data.nodeId,
                data.networkId,
                data.resourceUsage.toString(),
                data.resourceAllocated.toString()
        );

        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write log for node " + data.nodeId);
            e.printStackTrace();
        }
    }
}
