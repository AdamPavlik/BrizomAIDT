package com.brizom.aidt.moderatorservice.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.brizom.aidt.moderatorservice.dto.Signal;
import com.brizom.aidt.moderatorservice.service.ProcessingService;
import com.brizom.aidt.moderatorservice.service.SQSService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class LambdaHandler {

    private final Gson gson;
    private final ProcessingService processingService;
    private final SQSService sqsService;

    @Bean
    public Consumer<SQSEvent> moderate() {
        return event -> {
            event.getRecords().forEach(r -> {
                try {
                    processingService.processSignal(gson.fromJson(r.getBody(), Signal.class)).ifPresent(sqsService::sendOrder);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        };
    }


}
