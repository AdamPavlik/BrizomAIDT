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
            You are a daily crypto trader.
            
            Task – For %s as of %s, compile a concise, bullet-point dossier of objective, up-to-date data and sentiment indicators that could affect an intraday Hold/Buy/Sell decision.
            
            Include factual metrics only—no advice, opinions, or predictions—and do NOT list sources or links.
            
            Analytical Framework:
            - Timeframes: 1m, 15m, 1h, 4h, 1d, 1w, 1M
            - Required Technical Indicators: EMA, SMA, RSI, MACD, Bollinger Bands, ATR, OBV, VWAP, VOL
            - Optional Indicators (upon user request):
              - Chaikin Money Flow (CMF)
              - Money Flow Index (MFI)
              - Ichimoku Cloud
              - Fear and Greed Index
            
            Sources for Sentiment Analysis and News:
            - CryptoPanic, Whale Alert
            - Twitter (major crypto accounts and influential analysts)
            - Reddit (r/CryptoCurrency, r/BitcoinMarkets)
            - Press releases from the Fed, White House, European Union
            
            News Themes and Filters:
            - Significant ("Whale") Transactions
            - US Policy (FOMC, CPI, tariffs, sanctions)
            - EU Policy (crypto regulations, ECB announcements)
            
            Forecast Horizons and Approach:
            - Short-Term forecasts (up to 7 days): prioritize technical analysis and current market sentiment.
            - Mid-Term forecasts (3–6 months): incorporate macroeconomic context and fundamental developments.
            
            Cover:
            • Price— spot price, 24-h %s and 7-d %s
            • Volume— 24-h trading volume and its trend
            • Market cap & circulating supply
            • Key technicals— RSI, MACD, 20 / 50 / 200-day SMA, major support & resistance
            • Derivatives— funding rate, open interest
            • On-chain activity— active addresses, exchange inflows/outflows, large-holder (whale) moves
            • Sentiment gauges— Crypto Fear & Greed, social-media sentiment score
            • Recent catalysts (past 24 h)— news, regulatory actions, network upgrades, ETF flows
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
