package com.leea.optimizer.service;

import com.leea.optimizer.models.OptimizationRecommendation;
import com.leea.optimizer.models.TrafficData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OptimizationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${subscriber.api.url}")
    private String subscriberApiUrl; // NEED TO ADD ON SUBSCRIBER SIDE APP PROPRIETIES!!

    public List<OptimizationRecommendation> optimize() {
        TrafficData[] recentData = restTemplate.getForObject(subscriberApiUrl, TrafficData[].class);

        List<OptimizationRecommendation> recommendations = new ArrayList<>();
        if (recentData == null) return recommendations;

        for (TrafficData data : recentData) {
            recommendations.addAll(evaluateNode(data));
        }

        return recommendations;
    }

    private List<OptimizationRecommendation> evaluateNode(TrafficData data) {
        List<OptimizationRecommendation> recs = new ArrayList<>();
        recs.add(makeRecommendation(data, "CPU", data.getCpuUsage(), data.getCpuAllocated()));
        recs.add(makeRecommendation(data, "Memory", data.getMemoryUsage(), data.getMemoryAllocated()));
        recs.add(makeRecommendation(data, "Bandwidth", data.getBandwidthUsage(), data.getBandwidthAllocated()));
        return recs;
    }

    private OptimizationRecommendation makeRecommendation(TrafficData data, String type, double usage, double allocated) {
        double ratio = usage / allocated;
        double recommended = allocated;
        double increaseThreshold;
        double decreaseThreshold;
        double adjustmentFactor = 0.15; // 15% adjustment

        switch (type) {
            case "CPU":
                increaseThreshold = 0.85;
                decreaseThreshold = 0.40;
                break;
            case "Memory":
                increaseThreshold = 0.90;
                decreaseThreshold = 0.50;
                break;
            case "Bandwidth":
                increaseThreshold = 0.80;
                decreaseThreshold = 0.30;
                break;
            default:
                increaseThreshold = 0.80;
                decreaseThreshold = 0.30;
        }

        if (ratio > increaseThreshold) {
            recommended = allocated * (1 + adjustmentFactor);
        } else if (ratio < decreaseThreshold) {
            recommended = allocated * (1 - adjustmentFactor);
        }

        OptimizationRecommendation rec = new OptimizationRecommendation();
        rec.setNodeId(data.getNodeId());
        rec.setNetworkId(data.getNetworkId());
        rec.setResourceType(type);
        rec.setCurrentUsage(usage);
        rec.setCurrentAllocation(allocated);
        rec.setRecommendedAllocation(recommended);
        rec.setTimestamp(LocalDateTime.now());

        return rec;
    }
}

