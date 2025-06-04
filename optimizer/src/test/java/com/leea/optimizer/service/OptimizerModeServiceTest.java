package com.leea.optimizer.service;

import com.leea.optimizer.models.OptimizerMode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OptimizerModeServiceTest {

    @Test
    public void testDefaultModeIsAuto() {
        OptimizerModeService service = new OptimizerModeService();
        assertEquals(OptimizerMode.AUTO, service.getCurrentMode());
    }

    @Test
    public void testCanChangeToManual() {
        OptimizerModeService service = new OptimizerModeService();
        service.setMode(OptimizerMode.MANUAL);
        assertEquals(OptimizerMode.MANUAL, service.getCurrentMode());
    }
}
