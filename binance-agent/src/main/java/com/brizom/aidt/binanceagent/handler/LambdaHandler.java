package com.brizom.aidt.binanceagent.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.brizom.aidt.binanceagent.dto.OrderEvent;
import com.brizom.aidt.binanceagent.service.AgentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
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
                    throw new RuntimeException(ex);
                }
            });
        };
    }


}
