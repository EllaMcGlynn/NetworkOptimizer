package com.leea.optimizer.service;

import com.leea.optimizer.kafka.ActionKafkaProducer;
import com.leea.optimizer.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OptimizationService {

    @Autowired
    private KafkaTrafficDataConsumer kafkaConsumer;

    @Autowired
    private OptimizerModeService modeService;

    @Autowired
    private ActionKafkaProducer actionProducer;

    public List<OptimizationRecommendation> optimize() {
        List<TrafficData> recentData = kafkaConsumer.getRecentData();
        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        for (TrafficData data : recentData) {
            for (OptimizationRecommendation rec : evaluateNode(data)) {
                recommendations.add(rec);
                if (modeService.getCurrentMode() == OptimizerMode.AUTO) {
                    NodeStatus status = getNodeStatus(rec);
                    if (status == NodeStatus.HIGH || status == NodeStatus.LOW) {
                        OptimizerAction action = createActionFromRecommendation(rec, status);
                        actionProducer.sendAction(action);
                    }
                }
            }
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

    private NodeStatus getNodeStatus(OptimizationRecommendation rec) {
        double ratio = rec.getCurrentUsage() / rec.getCurrentAllocation();
        switch (rec.getResourceType()) {
            case "CPU":
                return ratio > 0.85 ? NodeStatus.HIGH : ratio < 0.40 ? NodeStatus.LOW : NodeStatus.GOOD;
            case "Memory":
                return ratio > 0.90 ? NodeStatus.HIGH : ratio < 0.50 ? NodeStatus.LOW : NodeStatus.GOOD;
            case "Bandwidth":
                return ratio > 0.80 ? NodeStatus.HIGH : ratio < 0.30 ? NodeStatus.LOW : NodeStatus.GOOD;
            default:
                return NodeStatus.GOOD;
        }
    }

    private OptimizerAction createActionFromRecommendation(OptimizationRecommendation rec, NodeStatus status) {
        double change = Math.abs(rec.getRecommendedAllocation() - rec.getCurrentAllocation());
        String actionType = status == NodeStatus.HIGH ? "INCREASE" : "DECREASE";

        OptimizerAction action = new OptimizerAction();
        action.setNodeId(rec.getNodeId());
        action.setResourceType(rec.getResourceType());
        action.setAmount(change);
        action.setActionType(actionType);
        action.setExecutedBy("OPTIMIZER");
        action.setTimestamp(LocalDateTime.now());

        return action;
    }

}
