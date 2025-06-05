package com.leea.generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leea.generator.model.OptimizerAction;
import com.leea.generator.service.DataGeneratorService;


@RestController
@RequestMapping("/api/generator")
public class ManualControl {
	
	@Autowired
	DataGeneratorService dataGen;
	
	@PostMapping("/optimize")
    public void setMode(@RequestBody OptimizerAction action) {
		dataGen.applyOptimizerAction(action);
    }
}
