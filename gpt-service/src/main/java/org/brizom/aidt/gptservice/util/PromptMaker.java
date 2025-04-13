package org.brizom.aidt.gptservice.util;

import lombok.val;
import org.brizom.aidt.gptservice.dto.binance.AccountSnapshot;
import org.brizom.aidt.gptservice.dto.binance.Snapshot;
import org.brizom.aidt.gptservice.dto.binance.Ticker24HWrapper;

import java.util.Comparator;
import java.util.stream.Collectors;

public class PromptMaker {



    public static String accountSnapshot(AccountSnapshot account) {
        val builder = new StringBuilder();
        account.getSnapshotVos().stream()
                .max(Comparator.comparing(Snapshot::getUpdateTime))
                .stream().map(Snapshot::getData)
                .findFirst()
                .ifPresent(snapshotData -> {
                    builder.append(snapshotData.getBalances()
                            .stream()
                            .map(balance -> String.format("Asset: %s - %s;", balance.getAsset(), balance.getFree()))
                            .collect(Collectors.joining("\n", "My current balances: \n", "\nTotal BTC balance: " + snapshotData.getTotalAssetOfBtc())));
                });
        return builder.toString();

    }


    public static String ticker24H(Ticker24HWrapper ticker24HWrapper) {
        return ticker24HWrapper.getTicker24H()
                .stream()
                .map(ticker -> String.format("Symbol: %s, Price Change Percent: %s, Open Price: %s, Last Price: %s, Volume: %s", ticker.getSymbol(), ticker.getPriceChangePercent(), ticker.getOpenPrice(), ticker.getLastPrice(), ticker.getVolume()))
                .collect(Collectors.joining("\n", "Last 24H prices: \n", "\n"));
    }


}
