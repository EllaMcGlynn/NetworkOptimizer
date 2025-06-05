package com.leea.optimizer.kafka;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class UnixTimestampDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Get the Unix timestamp as a long
        long timestamp = p.getLongValue();
        // Convert the timestamp to LocalDateTime
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
    }
}
