package org.brizom.aidt.kickoffservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brizom.aidt.kickoffservice.dto.AIProvider;
import org.brizom.aidt.kickoffservice.dto.KickoffEvent;
import org.brizom.aidt.kickoffservice.model.Coin;
import org.brizom.aidt.kickoffservice.repository.CoinRepository;
import org.brizom.aidt.kickoffservice.repository.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KickoffService {

    private final SettingRepository settingRepository;
    private final CoinRepository coinRepository;
    private final SQSService sqsService;

    public String kickOff() {
        log.info("Kickoff triggered");
        settingRepository.queryKickOffSettings().stream().forEach(settingPage -> settingPage.items().forEach(setting -> {
            log.info("Found setting for user: {}", setting.getUserId());
            if (!setting.isGenerateSignals()) {
                log.info("Setting is not generate signals, skipping: {}", setting.getUserId());
                return;
            }
            List<Coin> coins = coinRepository.queryCoins(setting.getUserId()).stream().flatMap(coinPage -> coinPage.items().stream()).toList();
            if (coins.isEmpty()) {
                log.info("No coins found for user: {}", setting.getUserId());
                return;
            }
            KickoffEvent event = KickoffEvent.builder().setting(setting).coins(coins).build();
            if (AIProvider.GPT.equals(setting.getAiProvider())) {
                log.info("Sending GPT message to SQS: {}", event);
                sqsService.sendMessage(event);
            }
        }));
        return "Kickoff triggered";
    }
}
