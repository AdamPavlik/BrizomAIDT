package com.brizom.aidt.moderatorservice.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.brizom.aidt.moderatorservice.dto.Signals;
import com.brizom.aidt.moderatorservice.service.ProcessingService;
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
    private final ProcessingService processingService;

    @Bean
    public Consumer<SQSEvent> moderate() {
        return event -> {
            event.getRecords().forEach(r -> {
                try {
                    processingService.processSignalEvent(gson.fromJson(r.getBody(), Signals.class));
                } catch (Exception ex) {
                    log.error("Error processing event: {}", r.getBody(), ex);
                }
            });
        };
    }


}
