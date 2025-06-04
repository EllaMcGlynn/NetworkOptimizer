package com.leea.optimizer.service;

import com.leea.optimizer.models.OptimizerMode;
import org.springframework.stereotype.Service;

@Service
public class OptimizerModeService {
    private OptimizerMode currentMode = OptimizerMode.AUTO;

    public OptimizerMode getCurrentMode() {
        return currentMode;
    }

    public void setMode(OptimizerMode mode) {
        this.currentMode = mode;
    }
}
