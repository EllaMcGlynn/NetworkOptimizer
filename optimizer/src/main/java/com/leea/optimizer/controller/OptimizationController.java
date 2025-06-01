package com.leea.optimizer.controller;

import com.leea.optimizer.models.OptimizationRecommendation;
import com.leea.optimizer.service.OptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimizer")
public class OptimizationController {

    @Autowired
    private OptimizationService optimizer;

    @GetMapping("/recommendations")
    public List<OptimizationRecommendation> getRecommendations() {
        return optimizer.optimize();
    }
}
