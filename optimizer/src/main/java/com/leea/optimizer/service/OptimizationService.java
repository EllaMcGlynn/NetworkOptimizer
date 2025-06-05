package com.leea.optimizer.service;

import com.leea.optimizer.kafka.ActionKafkaProducer;
import com.leea.optimizer.models.*;
import com.leea.optimizer.entity.OptimizerActionEntity;
import com.leea.optimizer.entity.OptimizationRecommendationEntity;
import com.leea.optimizer.repository.ActionRepository;
import com.leea.optimizer.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
        System.out.println("Optimiser called");
        List<TrafficData> recentData = kafkaConsumer.getRecentData();
        List<OptimizationRecommendation> allRecommendations = new ArrayList<>();
        Map<Integer, OptimizationRecommendation> bestRecPerNode = new java.util.HashMap<>();

        for (TrafficData data : recentData) {
            List<OptimizationRecommendation> recs = evaluateNode(data);

            for (OptimizationRecommendation rec : recs) {
                System.out.println("Optimiser checking ");
                allRecommendations.add(rec);
                saveRecommendationToDB(rec);

                if (modeService.getCurrentMode() == OptimizerMode.AUTO) {
                    NodeStatus status = getNodeStatus(rec);
                    if (status == NodeStatus.HIGH || status == NodeStatus.LOW) {
                        int nodeId = rec.getNodeId();
                        OptimizationRecommendation existing = bestRecPerNode.get(nodeId);

                        if (existing == null || isMoreCritical(rec, existing)) {
                            bestRecPerNode.put(nodeId, rec);
                        }
                    }
                }
            }
        }

        for (OptimizationRecommendation rec : bestRecPerNode.values()) {
            NodeStatus status = getNodeStatus(rec);
            OptimizerAction action = createActionFromRecommendation(rec, status);
            System.out.println("Optimiser Making suggestion");
            actionProducer.sendAction(action);
            saveActionToDB(action);
            System.out.println("Suggestion Sent");
        }

        return allRecommendations;
    }

    private boolean isMoreCritical(OptimizationRecommendation newRec, OptimizationRecommendation existingRec) {
        double newDeviation = Math.abs(newRec.getCurrentUsage() / newRec.getCurrentAllocation() - 1);
        double existingDeviation = Math.abs(existingRec.getCurrentUsage() / existingRec.getCurrentAllocation() - 1);
        return newDeviation > existingDeviation;
    }


    private List<OptimizationRecommendation> evaluateNode(TrafficData data) {
        List<OptimizationRecommendation> recs = new ArrayList<>();
        Map<String, Double> usage = data.getResourceUsage();
        Map<String, Double> allocated = data.getResourceAllocated();
        recs.add(makeRecommendation(data, "CPU", usage.get("cpu"), allocated.get("cpu")));
        recs.add(makeRecommendation(data, "Memory", usage.get("memory"), allocated.get("memory")));
        recs.add(makeRecommendation(data, "Bandwith", usage.get("bandwith"), allocated.get("bandwith")));
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
                increaseThreshold = 0.90;
                decreaseThreshold = 0.10;
                break;
            case "Memory":
                increaseThreshold = 0.90;
                decreaseThreshold = 0.10;
                break;
            case "Bandwith":
                increaseThreshold = 0.90;
                decreaseThreshold = 0.10;
                break;
            default:
                increaseThreshold = 0.90;
                decreaseThreshold = 0.10;
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
            case "Bandwith":
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

    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void scheduledOptimization() {
        optimize();
    }




}
