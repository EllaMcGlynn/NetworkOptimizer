package com.leea.optimizer.controller;

import com.leea.optimizer.models.OptimizationRecommendation;
import com.leea.optimizer.models.OptimizerMode;
import com.leea.optimizer.service.OptimizationService;
import com.leea.optimizer.service.OptimizerModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimizer")
public class OptimizationController {

    @Autowired
    private OptimizationService optimizer;

    @Autowired
    private OptimizerModeService modeService;

    @GetMapping("/recommendations")
    public List<OptimizationRecommendation> getRecommendations() {
        return optimizer.optimize();
    }

    @GetMapping("/mode")
    public OptimizerMode getMode() {
        return modeService.getCurrentMode();
    }

    @PostMapping("/mode")
    public void setMode(@RequestBody String mode) {
        modeService.setMode(OptimizerMode.valueOf(mode.toUpperCase()));
    }
}
