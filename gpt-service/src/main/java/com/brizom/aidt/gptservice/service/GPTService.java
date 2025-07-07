package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.Coin;
import com.brizom.aidt.gptservice.dto.Setting;
import com.brizom.aidt.gptservice.dto.Signals;
import com.brizom.aidt.gptservice.exception.AIException;
import com.brizom.aidt.gptservice.model.Prompt;
import com.brizom.aidt.gptservice.model.enums.PromptRole;
import com.brizom.aidt.gptservice.repository.PromptRepository;
import com.brizom.aidt.gptservice.util.GPTUtils;
import com.brizom.aidt.gptservice.util.PromptMaker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class GPTService {

    private final OpenAIClient client;
    private final GPTUtils gptUtils;
    private final ObjectMapper objectMapper;
    private final PromptRepository promptRepository;
    private final BinanceService binanceService;
    private final SQSService sqsService;
    private final GPTSearchService gptSearchService;
    private final DeepResearchService deepResearchService;

    public void generateSignals(Setting setting, List<Coin> coins) {
        log.info("Generating signals for user {} with setting {}", setting.getUserId(), setting);
        val targetedCoins = coins.stream().filter(Coin::isGenerateSignal).toList();
        log.info("Targeted coins: {}", targetedCoins);
        val prompts = new ArrayList<>(promptRepository.queryByUserId(setting.getUserId()).stream().filter(Prompt::isEnabled).toList());
        log.info("Prompts from DB: {}", prompts);

        if (prompts.isEmpty() || coins.isEmpty()) {
            log.info("No prompts found for user {} or no coins to generate signals for", setting.getUserId());
            return;
        }
        getPromptsFromGPTDeepResearch(prompts, targetedCoins, setting);
        getPromptsFromBinance(prompts, targetedCoins, setting);

        log.info("All Prompts: {}", prompts);

        val createParams = gptUtils.buildChatCompletionParams(setting, targetedCoins, prompts);
        val choices = client.chat().completions().create(createParams).choices();
        val choice = choices.stream().findAny().orElseThrow(() -> new AIException("No response from GPT"));
        val content = choice.message().content().stream().findAny().orElseThrow(() -> new AIException("No content from GPT"));

        try {
            Signals signals = objectMapper.readValue(content, Signals.class);
            signals.setSetting(setting);
            signals.setCoins(targetedCoins);
            log.info("Generated signals: {}", signals);
            sqsService.sendSignalMessages(signals);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert signals to JSON", e);
            throw new AIException("Cannot parse structured response", e);
        }
    }

    private void getPromptsFromBinance(List<Prompt> prompts, List<Coin> coins, Setting setting) {
        CompletableFuture<Prompt> snapshotFuture = null;
        CompletableFuture<Prompt> tickerFuture = null;

        if (setting.isIncludeBalances()) {
            log.info("Generating account snapshot prompt");
            snapshotFuture = CompletableFuture.supplyAsync(() ->
                    Prompt.builder()
                            .prompt(PromptMaker.accountSnapshot(binanceService.getAccountSnapshot(setting.getUserId())))
                            .role(PromptRole.USER)
                            .build());
        }
        if (setting.isIncludeLiveData()) {
            log.info("Generating ticker24H prompt");
            tickerFuture = CompletableFuture.supplyAsync(() ->
                    Prompt.builder()
                            .prompt(PromptMaker.ticker24H(binanceService.getTicker24H(coins.stream().map(Coin::getSymbol).map(coin -> coin + setting.getStableCoin()).toList())))
                            .role(PromptRole.USER)
                            .build());
        }

        prompts.addAll(Stream.of(snapshotFuture, tickerFuture)
                .filter(Objects::nonNull)
                .map(CompletableFuture::join)
                .filter(prompt -> !Strings.isBlank(prompt.getPrompt()))
                .toList());
    }

    private void getPromptsFromGPTDeepResearch(List<Prompt> prompts, List<Coin> coins, Setting setting) {
        prompts.add(Prompt.builder().prompt(deepResearchService.deepResearch(coins)).role(PromptRole.USER).build());
    }


}
