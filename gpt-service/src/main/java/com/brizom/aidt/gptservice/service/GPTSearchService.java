package com.brizom.aidt.gptservice.service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GPTSearchService {

    private final OpenAIClient client;

    private static final String PROMPT_TEMPLATE = """
            For %s as of %s (UTC) gather only the fresh, objective facts most likely to influence today’s price action.
            """;

    private static final String INSTRUCTION = """
            ROLE
            You are a market-data scout for an intraday crypto-trading engine.
            
            MISSION
            For selected coin gather only the fresh, objective facts most likely to influence today’s price action.
            - No advice, opinions, predictions, links, or long prose.
            - Prefer data < 3 h old; ignore anything > 24 h.
            - Cross-check key numbers; if reputable sources disagree, keep both and add “CONFLICT”.
            
            RULES
            - Numbers over prose—give concrete values (USD, percentage, units).
            - Filter out promotional tweets, unsourced rumors, editorials.
            - Search for different indicators like: CMF, MFI, Ichimoku, or extended macro themes and others.
            - Search for at least 5 different indicators.
            - Do not list sources or URLs.
            - Important: Keep total bullet count around 40.
            - Important: Find at least 8 - 15 indicators.
            - Discard anything older than 24 h; prefer metrics ≤ 3 h old.
            - For each header write at least 10 bullet points.
            - Feel free to add any other important information that you find.
            
            OUTPUT FORMAT
            - Write comprehensive bullet pints and group them by nature.
            - Write result in understandable way for AI.
            
            Include next header's but to limit: PRICE & VOLUME, , ON-CHAIN, TECHNICAL HIGHLIGHTS, SENTIMENT, NEWS & MACRO, OTHER IMPORTANT FACTORS.
            """;

    public String getGptInternetSearch(String coin) {
        String prompt = String.format(PROMPT_TEMPLATE, coin, ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE), "%", "%");
        WebSearchTool webSearchTool = WebSearchTool.builder()
                .type(WebSearchTool.Type.WEB_SEARCH_PREVIEW)
                .searchContextSize(WebSearchTool.SearchContextSize.HIGH)
                .build();

        ResponseCreateParams params = ResponseCreateParams.builder()
                .addTool(webSearchTool).model(ChatModel.GPT_4_1)
                .maxOutputTokens(4096)
                .temperature(0.1)
                .topP(0.2)
                .instructions(INSTRUCTION)
                .input(prompt)
                .build();
        return client.responses().create(params).output().stream()
                .filter(ResponseOutputItem::isMessage)
                .map(ResponseOutputItem::message)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(responseOutputMessage -> responseOutputMessage.content().stream())
                .map(ResponseOutputMessage.Content::asOutputText)
                .map(ResponseOutputText::text)
                .findFirst()
                .orElse("");

    }

}
