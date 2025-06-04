package com.leea.optimizer.service;

import com.leea.optimizer.kafka.ActionKafkaProducer;
import com.leea.optimizer.models.*;
import com.leea.optimizer.entity.OptimizerActionEntity;
import com.leea.optimizer.entity.OptimizationRecommendationEntity;
import com.leea.optimizer.repository.ActionRepository;
import com.leea.optimizer.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OptimizationService {

    @Autowired
    private KafkaTrafficDataConsumer kafkaConsumer;

    @Autowired
    private OptimizerModeService modeService;

    @Autowired
    private ActionKafkaProducer actionProducer;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private ActionRepository actionRepository;

    public List<OptimizationRecommendation> optimize() {
        List<TrafficData> recentData = kafkaConsumer.getRecentData();
        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        for (TrafficData data : recentData) {
            for (OptimizationRecommendation rec : evaluateNode(data)) {
                recommendations.add(rec);
                saveRecommendationToDB(rec);

                if (modeService.getCurrentMode() == OptimizerMode.AUTO) {
                    NodeStatus status = getNodeStatus(rec);
                    if (status == NodeStatus.HIGH || status == NodeStatus.LOW) {
                        OptimizerAction action = createActionFromRecommendation(rec, status);
                        actionProducer.sendAction(action);
                        saveActionToDB(action);
                    }
                }
            }
        }
        return recommendations;
    }

    private List<OptimizationRecommendation> evaluateNode(TrafficData data) {
        List<OptimizationRecommendation> recs = new ArrayList<>();
        Map<String, Double> usage = data.getResourceUsage();
        Map<String, Double> allocated = data.getResourceAllocated();
        recs.add(makeRecommendation(data, "CPU", usage.get("cpu"), allocated.get("cpu")));
        recs.add(makeRecommendation(data, "Memory", usage.get("memory"), allocated.get("memory")));
        recs.add(makeRecommendation(data, "Bandwidth", usage.get("bandwidth"), allocated.get("bandwidth")));
        return recs;
    }

    private OptimizationRecommendation makeRecommendation(TrafficData data, String type, double usage, double allocated) {
        double ratio = usage / allocated;
        double recommended = allocated;
        double increaseThreshold;
        double decreaseThreshold;
        double adjustmentFactor = 0.15;

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

    private void saveRecommendationToDB(OptimizationRecommendation rec) {
        OptimizationRecommendationEntity entity = new OptimizationRecommendationEntity();
        entity.setNodeId(rec.getNodeId());
        entity.setNetworkId(rec.getNetworkId());
        entity.setResourceType(rec.getResourceType());
        entity.setCurrentUsage(rec.getCurrentUsage());
        entity.setCurrentAllocation(rec.getCurrentAllocation());
        entity.setRecommendedAllocation(rec.getRecommendedAllocation());
        entity.setTimestamp(rec.getTimestamp());
        recommendationRepository.save(entity);
    }

    private void saveActionToDB(OptimizerAction action) {
        OptimizerActionEntity entity = new OptimizerActionEntity();
        entity.setNodeId(action.getNodeId());
        entity.setResourceType(action.getResourceType());
        entity.setAmount(action.getAmount());
        entity.setActionType(action.getActionType());
        entity.setExecutedBy(action.getExecutedBy());
        entity.setTimestamp(action.getTimestamp());
        actionRepository.save(entity);
    }

}
