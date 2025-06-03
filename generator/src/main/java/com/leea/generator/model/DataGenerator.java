package com.leea.generator.model;

import java.time.Instant;
import java.util.Map;


public class DataGenerator {
    public int nodeId;
    public int networkId;
    public Map<String, Double> resourceUsage;
    public Map<String, Double> resourceAllocated;
    public Instant timestamp;
}
