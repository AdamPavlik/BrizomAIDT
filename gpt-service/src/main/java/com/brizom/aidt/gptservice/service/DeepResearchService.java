package com.brizom.aidt.gptservice.service;


import com.brizom.aidt.gptservice.dto.Coin;
import com.openai.client.OpenAIClient;
import com.openai.models.ResponsesModel;
import com.openai.models.responses.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeepResearchService {

    private final OpenAIClient client;

    public String deepResearch(List<Coin> coins) {
        log.info("Starting deep research for coins {}", coins);
        WebSearchTool webSearchTool = WebSearchTool.builder()
                .type(WebSearchTool.Type.WEB_SEARCH_PREVIEW)
                .searchContextSize(WebSearchTool.SearchContextSize.MEDIUM)
                .build();

        String prompt = "Find all necessary information that will help you make decision to Hold, buy or sell cryptocurrencies today to make profit.";
        ResponseCreateParams params = ResponseCreateParams.builder()
                .addTool(webSearchTool).model(ResponsesModel.ResponsesOnlyModel.O3_DEEP_RESEARCH)
                .instructions(buildInstructions(coins))
                .input(prompt)
                .build();

        String deepResearchResult = client.responses().create(params).output().stream()
                .filter(responseOutputItem -> responseOutputItem.message().isPresent())
                .map(ResponseOutputItem::asMessage)
                .flatMap(responseOutputMessage -> responseOutputMessage.content().stream())
                .map(ResponseOutputMessage.Content::asOutputText)
                .map(ResponseOutputText::text)
                .collect(Collectors.joining(","));
        log.info("Deep research result: {}", deepResearchResult);
        return deepResearchResult;
    }

    private String buildInstructions(List<Coin> coins) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < coins.size(); i++) {
            builder.append(i + 1).append(". ").append(coins.get(i).getSymbol()).append("\n");
        }

        String instructions = String.format("""
                Today's date: %s.
                You are a daily crypto trader.
                
                Focus on next currencies:
                %s
                
                Guidance:
                - Don not make predictions, just collect relative information and indexes.
                - Do not include sources or links.
                - Keep similar structure to next template:
                
                Crypto Daily Update - Date
                Cryptocurrency name
                General overview
                    Technical Indicators
                    On-Chain Metrics
                    Market Sentiment
                    Key News or Events
                    Spot and Derivatives Data
                """, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE), builder);
        log.info("Deep research instructions: {}", instructions);
        return instructions;
    }


}
