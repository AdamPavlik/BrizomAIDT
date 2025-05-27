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
            ROLE
            You are a market-data scout for an intraday crypto-trading engine.
            
            MISSION
            For %s as of %s (UTC) gather only the fresh, objective facts most likely to influence today’s price action.
            • No advice, opinions, predictions, links, or long prose.
            • Prefer data < 3 h old; ignore anything > 24 h.
            • Cross-check key numbers; if reputable sources disagree, keep both and add “CONFLICT”.
            
            RULES
            1. Numbers over prose—give concrete values (USD, percentage, units).
            2. Filter out promotional tweets, unsourced rumours, editorials.
            3. Optionally search for different indicators like: CMF, MFI, Ichimoku, or extended macro themes.
            4. Do not list sources or URLs.
            5. Keep total bullet count ≤ 40.
            6. Discard anything older than 24 h; prefer metrics ≤ 3 h old.
            
            OUTPUT FORMAT
            Write short bullet lines grouped under the six headers below.
            Start every bullet with the UPPER-CASE tag shown (easy downstream parsing).
            Omit any header that truly has nothing material—do not pad with trivia.
            
            HEADERS
            PRICE & VOLUME        (tags: PRICE:, VOL:)
            DERIVATIVES           (FUNDING:, OI:)
            ON-CHAIN              (INFLOW:, OUTFLOW:, WHALE:)
            TECHNICAL HIGHLIGHTS  (RSI:, MACD:, S/R:, BBW:, etc.)
            SENTIMENT             (FEAR_GREED:, TWITTER:, REDDIT:, etc.)
            NEWS & MACRO          (NEWS:, MACRO:, WHALE_TX:, POLICY:)
            """;

    public String getGptInternetSearch(String coin) {
        String prompt = String.format(PROMPT_TEMPLATE, coin, ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE), "%", "%");
        WebSearchTool webSearchTool = WebSearchTool.builder()
                .type(WebSearchTool.Type.WEB_SEARCH_PREVIEW)
                .searchContextSize(WebSearchTool.SearchContextSize.HIGH)
                .build();

        ResponseCreateParams params = ResponseCreateParams.builder()
                .addTool(webSearchTool).model(ChatModel.GPT_4_1)
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
