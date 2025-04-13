package org.brizom.aidt.gptservice.controller;

import lombok.AllArgsConstructor;
import org.brizom.aidt.gptservice.dto.Signals;
import org.brizom.aidt.gptservice.service.GPTService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/signals")
@AllArgsConstructor
public class SignalController {

    private final GPTService gptService;

    @GetMapping
    public ResponseEntity<Signals> getSignals() {
        Signals signals = gptService.generateSignals();
        return ResponseEntity.ok(signals);
    }

}
