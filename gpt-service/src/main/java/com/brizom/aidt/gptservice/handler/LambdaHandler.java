package com.brizom.aidt.gptservice.handler;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.brizom.aidt.gptservice.dto.KickoffEvent;
import com.brizom.aidt.gptservice.service.DeepResearchService;
import com.brizom.aidt.gptservice.service.GPTService;
import com.brizom.aidt.gptservice.service.SQSService;
import com.google.gson.Gson;
import com.openai.models.responses.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@AllArgsConstructor
@Slf4j
public class LambdaHandler {

    private final GPTService gptService;
    private final DeepResearchService deepResearchService;
    private final SQSService sqsService;
    private final Gson gson;

    @Bean
    public Consumer<SQSEvent> moderate() {
        return event -> {
            event.getRecords().forEach(r -> {
                try {
                    val kickoffEvent = gson.fromJson(r.getBody(), KickoffEvent.class);
                    if (Strings.isBlank(kickoffEvent.getMetadata().getDeepResearchId())) {
                        log.info("No deep research id found, event: {}", r);
                        log.info("Starting deep research for coins: {}", kickoffEvent.getCoins());
                        val researchId = deepResearchService.startDeepResearchOnBackground(kickoffEvent.getCoins());
                        log.info("Deep research started, id: {}", researchId);
                        kickoffEvent.getMetadata().setDeepResearchId(researchId);
                        sqsService.sendKickoffEven(kickoffEvent, 900);
                    } else {
                        Response response = deepResearchService.retrieveDeepResearch(kickoffEvent.getMetadata().getDeepResearchId());
                        response.status().ifPresent(status -> {
                            if (status.asString().equals("completed")) {
                                log.info("Deep research completed, event: {}", r);
                                gptService.generateSignals(kickoffEvent.getSetting(), kickoffEvent.getCoins(), deepResearchService.mupToString(response));
                            } else if (status.asString().equals("in_progress") || status.asString().equals("queued")) {
                                sqsService.sendKickoffEven(kickoffEvent, 900);
                            } else {
                                log.info("Deep research failed, event: {}, status: {}", r, status.asString());
                            }
                        });
                    }
                } catch (Exception ex) {
                    log.error("Failed to generate signals, event: {}, messageL {}", r, ex.getMessage());
                }
            });
        };
    }

}
