package com.brizom.aidt.binanceagent.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.brizom.aidt.binanceagent.dto.OrderEvent;
import com.brizom.aidt.binanceagent.service.AgentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LambdaHandler {

    private final Gson gson;
    private final AgentService agentService;

    @Bean
    public Consumer<SQSEvent> executeOrder() {
        return event -> {
            event.getRecords().forEach(r -> {
                try {
                    agentService.newMarketOrder(gson.fromJson(r.getBody(), OrderEvent.class));
                } catch (Exception ex) {
                    log.error("Failed to execute order, event: {}, messageL {}", r, ex.getMessage());
                }
            });
        };
    }


}
