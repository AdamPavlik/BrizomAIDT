package com.brizom.aidt.gptservice.util;

import com.brizom.aidt.gptservice.model.Coin;
import com.brizom.aidt.gptservice.model.Prompt;
import com.openai.core.JsonValue;
import com.openai.models.*;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GPTUtils {

    public ResponseFormatJsonSchema.JsonSchema.Schema buildGptSignalsSchema(List<String> coins) {
        return ResponseFormatJsonSchema.JsonSchema.Schema.builder()
                .putAdditionalProperty("type", JsonValue.from("object"))
                .putAdditionalProperty("title", JsonValue.from("Trading Signal Schema"))
                .putAdditionalProperty("description", JsonValue.from("This JSON schema defines the structure for a list of trading signal. Each signal is designed to provide a recommendation for a specific coin, including the recommended trading action (BUY, SELL, or HOLD), a detailed rationale, and a confidence level (as a percentage between 0 and 100). The schema ensures that each suggestion is unique and that the list covers all required coins."))
                .putAdditionalProperty("additionalProperties", JsonValue.from(false))
                .putAdditionalProperty("properties", JsonValue.from(
                        Map.of("signals", Map.of(
                                "type", "array",
                                "description", "An array of trading signals. Each signal object provides a recommendation for trading a specific coin. It includes the coin identifier, the recommended trading action (BUY, SELL, or HOLD), a detailed explanation for the recommendation, and a confidence level as a percentage (0 to 100). The array is expected to have as many unique items as there are coins.",
//                                "minItems", coins.size(),
//                                "uniqueItems", true,
                                "items", Map.of(
                                        "type", "object",
                                        "description", "A single trading signal, detailing the coin, recommended action, rationale, and confidence percentage.",
                                        "additionalProperties", false,
                                        "properties", Map.of(
                                                "coin", Map.of(
                                                        "type", "string",
                                                        "description", "The unique identifier for the coin, typically formatted as 'BASE' (e.g., BTC), representing the specific trading coin for which the signal is provided",
                                                        "enum", coins,
                                                        "example", "BTC"),
                                                "action", Map.of(
                                                        "type", "string",
                                                        "description", "The recommended trading action for the coin. The value must be one of: BUY, SELL, or HOLD.",
                                                        "enum", List.of("BUY", "SELL", "HOLD"),
                                                        "example", "BUY"),
                                                "reason", Map.of(
                                                        "type", "string",
                                                        "description", "A detailed explanation outlining the analysis or factors that led to the trading recommendation.",
                                                        "example", "Based on recent market trends and volume analysis."),
                                                "confidence", Map.of(
                                                        "type", "number",
                                                        "description", "A numeric value representing the confidence level of the recommendation, expressed as a percentage between 0 (no confidence) and 100 (full confidence).",
//                                                        "minimum", 0,
//                                                        "maximum", 100,
                                                        "example", 85)
                                        ),
                                        "required", List.of("coin", "action", "reason", "confidence")
                                )
                        ))
                ))
                .putAdditionalProperty("required", JsonValue.from(List.of("signals")))
                .build();
    }


    
    
    public ChatCompletionCreateParams buildChatCompletionParams(ChatModel model, ChatCompletionReasoningEffort effort, int maxTokens, List<Coin> coins, List<Prompt> prompts) {
        val createParams = ChatCompletionCreateParams.builder()
                .model(model)
                .reasoningEffort(effort)
                .maxCompletionTokens(maxTokens)
                .responseFormat(buildResponseFormat(coins));
        addGenericPrompts(createParams, prompts);
        addTargetedCoins(createParams, coins);
        return createParams.build();
    }


    private void addGenericPrompts(ChatCompletionCreateParams.Builder builder, List<Prompt> prompts) {
        for (Prompt prompt : prompts) {
            switch (prompt.getRole()) {
                case USER -> builder.addMessage(ChatCompletionUserMessageParam.builder().content(prompt.getPrompt()).build()).build();
                case DEVELOPER -> builder.addMessage(ChatCompletionDeveloperMessageParam.builder().content(prompt.getPrompt()).build()).build();
                case ASSISTANT -> builder.addMessage(ChatCompletionAssistantMessageParam.builder().content(prompt.getPrompt()).build()).build();
                case SYSTEM -> builder.addMessage(ChatCompletionSystemMessageParam.builder().content(prompt.getPrompt()).build()).build();
                default -> throw new IllegalArgumentException("Unsupported role: " + prompt.getRole());
            }
        }
    }

    private void addTargetedCoins(ChatCompletionCreateParams.Builder builder, List<Coin> targetedCoins) {
        val coinsPrompt = targetedCoins.stream()
                .map(Coin::getSymbol)
                .collect(Collectors.joining(", ", "You are looking for: ", "."));
        builder.addMessage(ChatCompletionDeveloperMessageParam.builder().content(coinsPrompt).build());
    }


    private ResponseFormatJsonSchema buildResponseFormat(List<Coin> coins) {
        return ResponseFormatJsonSchema.builder()
                .jsonSchema(ResponseFormatJsonSchema.JsonSchema.builder()
                        .name("trading-signal-schema")
                        .schema(buildGptSignalsSchema(coins.stream().map(Coin::getSymbol).toList()))
                        .strict(true)
                        .build())
                .build();
    }

}
