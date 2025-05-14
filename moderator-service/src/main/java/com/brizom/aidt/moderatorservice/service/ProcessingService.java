package com.brizom.aidt.moderatorservice.service;

import com.brizom.aidt.moderatorservice.dto.*;
import com.brizom.aidt.moderatorservice.dto.account.AccountSnapshot;
import com.brizom.aidt.moderatorservice.dto.account.Balance;
import com.brizom.aidt.moderatorservice.dto.account.Snapshot;
import com.brizom.aidt.moderatorservice.dto.exchange.ExchangeInfo;
import com.brizom.aidt.moderatorservice.dto.exchange.Filter;
import com.brizom.aidt.moderatorservice.repository.OrderHistoryRepository;
import com.brizom.aidt.moderatorservice.repository.SignalHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private static final BigDecimal BUY_ALLOCATION = BigDecimal.valueOf(0.20);
    private static final BigDecimal SELL_ALLOCATION = BigDecimal.ONE;

    private final BinanceService binanceService;
    private final SQSService sqsService;
    private final SesService sesService;
    private final OrderHistoryRepository orderHistoryRepository;
    private final SignalHistoryRepository signalHistoryRepository;

    public void processSignalEvent(Signals signalsWrapper) {
        val signals = signalsWrapper.getSignals();
        val setting = signalsWrapper.getSetting();
        val coinsMap = signalsWrapper.getCoins().stream().collect(Collectors.toMap(Coin::getSymbol, Function.identity()));

        signalHistoryRepository.storeSignals(signals, setting.getUserId());
        sendSignalsEmails(signals, setting, coinsMap);

        if (setting.isExecuteOrders()) {
            overwriteHoldOption(signals, signalsWrapper.getSetting().getOnHoldAction());
            val actionSignals = signals.stream()
                    .filter(signal -> !Action.HOLD.equals(signal.getAction()))
                    .filter(signal -> coinsMap.get(signal.getCoin()).isExecuteOrder())
                    .filter(signal -> (signal.getAction() == Action.BUY && signal.getConfidence() > setting.getConfidenceToBuy()) ||
                            (signal.getAction() == Action.SELL && signal.getConfidence() > setting.getConfidenceToSell()))
                    .toList();
            val accountSnapshotResp = binanceService.getAccountSnapshot(setting.getUserId());

            if (accountSnapshotResp != null && accountSnapshotResp.getSnapshotVos() != null && !accountSnapshotResp.getSnapshotVos().isEmpty()) {
                List<Order> orders = new ArrayList<>();
                val symbols = actionSignals.stream().map(Signal::getCoin).map(coin -> coin + setting.getStableCoin()).toList();
                val exchangeInfo = binanceService.exchangeInfo(symbols);
                val accountSnapshot = getLastSnapshot(accountSnapshotResp);

                actionSignals.forEach(signal -> {
                    if (signal.getAction() == Action.BUY) {
                        processBuyOrder(signal, accountSnapshot, exchangeInfo, setting).ifPresent(orders::add);
                    }
                    if (signal.getAction() == Action.SELL) {
                        processSellOrder(signal, accountSnapshot, exchangeInfo, setting).ifPresent(orders::add);
                    }
                });

                if (!orders.isEmpty()) {
                    orderHistoryRepository.storeOrders(orders, setting.getUserId());
                    val orderEvent = OrderEvent.builder()
                            .setting(setting)
                            .orders(orders)
                            .build();
                    sqsService.sendOrder(orderEvent);
                }

            }
        }


    }

    private Optional<Order> processBuyOrder(Signal signal, Snapshot snapshot, ExchangeInfo exchangeInfo, Setting setting) {
        String symbol = signal.getCoin() + setting.getStableCoin().name();
        BigDecimal freeQuoteAsset = snapshot.getData().getBalances().stream()
                .filter(balance -> balance.getAsset().equalsIgnoreCase(setting.getStableCoin().name()))
                .map(Balance::getFree)
                .map(BigDecimal::valueOf)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        if (freeQuoteAsset.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        } else {
            freeQuoteAsset = freeQuoteAsset.multiply(BigDecimal.valueOf(setting.getBalanceUtilization())).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
        }

        BigDecimal minNotional = exchangeInfo.getSymbols().stream()
                .filter(exchangeSymbol -> symbol.equalsIgnoreCase(exchangeSymbol.getSymbol()))
                .flatMap(exchangeSymbol -> exchangeSymbol.getFilters().stream())
                .filter(filter -> "NOTIONAL".equals(filter.getFilterType()))
                .map(filter -> new BigDecimal(filter.getMinNotional()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("NOTIONAL missing for " + symbol));

        BigDecimal amountQuoteAsset = freeQuoteAsset.multiply(BUY_ALLOCATION).setScale(2, RoundingMode.DOWN);

        if (amountQuoteAsset.compareTo(minNotional) < 0) {
            if (freeQuoteAsset.compareTo(minNotional) >= 0) {
                amountQuoteAsset = minNotional;
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(Order.builder()
                .symbol(symbol)
                .side(OrderSide.BUY)
                .type(OrderType.MARKET)
                .quoteOrderQty(amountQuoteAsset.doubleValue())
                .build());
    }

    private Optional<Order> processSellOrder(Signal signal, Snapshot snapshot, ExchangeInfo exchangeInfo, Setting setting) {
        String symbol = signal.getCoin() + setting.getStableCoin().name();

        BigDecimal freeBase = snapshot.getData().getBalances().stream()
                .filter(balance -> balance.getAsset().equalsIgnoreCase(signal.getCoin()))
                .map(Balance::getFree)
                .map(BigDecimal::valueOf)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        if (freeBase.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        BigDecimal rawQty = freeBase.multiply(SELL_ALLOCATION);

        Filter lot = exchangeInfo.getSymbols().stream()
                .filter(exchangeSymbol -> symbol.equalsIgnoreCase(exchangeSymbol.getSymbol()))
                .flatMap(exchangeSymbol -> exchangeSymbol.getFilters().stream())
                .filter(filter -> "LOT_SIZE".equals(filter.getFilterType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("LOT_SIZE filter missing for " + symbol));

        BigDecimal step = new BigDecimal(lot.getStepSize());
        BigDecimal minQty = new BigDecimal(lot.getMinQty());

        BigDecimal qty = rawQty.divide(step, 0, RoundingMode.DOWN).multiply(step).stripTrailingZeros();

        if (qty.compareTo(minQty) < 0) {
            if (freeBase.compareTo(minQty) >= 0) {
                qty = minQty;
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(Order.builder()
                .symbol(symbol)
                .type(OrderType.MARKET)
                .side(OrderSide.SELL)
                .quantity(qty.doubleValue())
                .build());
    }

    private void overwriteHoldOption(List<Signal> signals, Action action) {
        if (Action.HOLD.equals(action)) {
            return;
        }
        signals.stream()
                .filter(signal -> Action.HOLD.equals(signal.getAction()))
                .forEach(signal -> signal.setAction(action));
    }

    private Snapshot getLastSnapshot(AccountSnapshot accountSnapshot) {
        return accountSnapshot.getSnapshotVos().stream().max(Comparator.comparing(Snapshot::getUpdateTime)).orElseThrow();
    }

    private void sendSignalsEmails(List<Signal> signals, Setting setting, Map<String, Coin> coinsMap) {
        if (setting.isSendEmails()) {
            sesService.sendSignalsEmails(signals.stream().filter(signal -> coinsMap.get(signal.getCoin()).isSendEmail()).toList(), setting.getEmail());
        }
    }


}
