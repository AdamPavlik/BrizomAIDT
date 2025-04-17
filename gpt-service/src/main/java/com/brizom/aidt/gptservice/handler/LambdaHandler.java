package com.brizom.aidt.gptservice.handler;

import com.brizom.aidt.gptservice.dto.Signals;
import com.brizom.aidt.gptservice.service.GPTService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@AllArgsConstructor
public class LambdaHandler {

    private final GPTService gptService;

    @Bean
    public Supplier<Signals> signals() {
        return gptService::generateSignals;
    }

}
