package com.brizom.aidt.gptservice.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.brizom.aidt.gptservice.dto.KickoffEvent;
import com.brizom.aidt.gptservice.service.GPTService;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@AllArgsConstructor
@Slf4j
public class LambdaHandler {

    private final GPTService gptService;
    private final Gson gson;

    @Bean
    public Consumer<SQSEvent> moderate() {
        return event -> {
            event.getRecords().forEach(r -> {
                try {
                    val kickoffEvent = gson.fromJson(r.getBody(), KickoffEvent.class);
                    gptService.generateSignals(kickoffEvent.getSetting(), kickoffEvent.getCoins());
                } catch (Exception ex) {
                    log.error("Failed to generate signals, event: {}, messageL {}", r, ex.getMessage());
                }
            });
        };
    }

}
