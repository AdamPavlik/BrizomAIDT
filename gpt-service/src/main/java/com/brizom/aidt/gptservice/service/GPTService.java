package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.Signals;
import com.brizom.aidt.gptservice.exception.AIException;
import com.brizom.aidt.gptservice.model.Coin;
import com.brizom.aidt.gptservice.model.Prompt;
import com.brizom.aidt.gptservice.model.enums.PromptRole;
import com.brizom.aidt.gptservice.repository.CoinRepository;
import com.brizom.aidt.gptservice.repository.PromptRepository;
import com.brizom.aidt.gptservice.util.GPTUtils;
import com.brizom.aidt.gptservice.util.PromptMaker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.ReasoningEffort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private final CoinRepository coinRepository;
    private final BinanceService binanceService;
    private final SQSService sqsService;
    private final SesService sesService;

    public Signals generateSignals() {
        val targetedCoins = coinRepository.findAll();
        val prompts = new ArrayList<>(promptRepository.findAll());

        getPromptsFromBinance(prompts, targetedCoins);

        val createParams = gptUtils.buildChatCompletionParams(ChatModel.O3, ReasoningEffort.HIGH, 32000, targetedCoins, prompts);
        val choices = client.chat().completions().create(createParams).choices();
        val choice = choices.stream().findAny().orElseThrow(() -> new AIException("No response from GPT"));
        val content = choice.message().content().stream().findAny().orElseThrow(() -> new AIException("No content from GPT"));

        try {
            Signals signals = objectMapper.readValue(content, Signals.class);
            sqsService.sendSignalMessages(signals);
            sesService.sendSignalsEmails(signals);
            return signals;
        } catch (JsonProcessingException e) {
            log.error("Failed to convert signals to JSON", e);
            throw new AIException("Cannot parse structured response", e);
        }
    }

    private void getPromptsFromBinance(List<Prompt> prompts, List<Coin> coins) {
        CompletableFuture<Prompt> snapshotFuture = CompletableFuture.supplyAsync(() ->
                Prompt.builder()
                        .prompt(PromptMaker.accountSnapshot(binanceService.getAccountSnapshot()))
                        .role(PromptRole.USER)
                        .build());

        CompletableFuture<Prompt> tickerFuture = CompletableFuture.supplyAsync(() ->
                Prompt.builder()
                        .prompt(PromptMaker.ticker24H(binanceService.getTicker24H(coins.stream().map(Coin::getSymbol).map(coin  -> coin + "USDT").toList())))
                        .role(PromptRole.USER)
                        .build());

        prompts.addAll(Stream.of(snapshotFuture, tickerFuture)
                .map(CompletableFuture::join)
                .toList());
    }


}
