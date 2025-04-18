package com.brizom.aidt.moderatorservice.service;

import com.brizom.aidt.moderatorservice.dto.*;
import com.brizom.aidt.moderatorservice.dto.account.AccountSnapshot;
import com.brizom.aidt.moderatorservice.dto.account.Balance;
import com.brizom.aidt.moderatorservice.dto.account.Snapshot;
import com.brizom.aidt.moderatorservice.dto.exchange.ExchangeInfo;
import com.brizom.aidt.moderatorservice.dto.exchange.Filter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private static final String QUOTE_ASSET = "USDT";
    private static final BigDecimal BUY_ALLOCATION = BigDecimal.valueOf(0.20);
    private static final BigDecimal SELL_ALLOCATION = BigDecimal.ONE;

    private final BinanceService binanceService;

    public Optional<Order> processSignal(Signal signal) {
        if (signal.getAction() == Action.HOLD) {
            return Optional.empty();
        }

        val exchangeInfo = binanceService.exchangeInfo(List.of(signal.getCoin() + QUOTE_ASSET));
        val accountSnapshot = getLastSnapshot(binanceService.getAccountSnapshot());

        return switch (signal.getAction()) {
            case BUY -> processBuyOrder(signal, accountSnapshot, exchangeInfo);
            case SELL -> processSellOrder(signal, accountSnapshot, exchangeInfo);
            default -> Optional.empty();
        };
    }

    private Optional<Order> processBuyOrder(Signal signal, Snapshot snapshot, ExchangeInfo exchangeInfo) {
        String symbol = signal.getCoin() + QUOTE_ASSET;
        BigDecimal freeQuoteAsset = snapshot.getData().getBalances().stream()
                .filter(balance -> balance.getAsset().equalsIgnoreCase(QUOTE_ASSET))
                .map(Balance::getFree)
                .map(BigDecimal::valueOf)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        if (freeQuoteAsset.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
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

    private Optional<Order> processSellOrder(Signal signal, Snapshot snapshot, ExchangeInfo exchangeInfo) {
        String symbol = signal.getCoin() + QUOTE_ASSET;

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

    private Snapshot getLastSnapshot(AccountSnapshot accountSnapshot) {
        return accountSnapshot.getSnapshotVos().stream().max(Comparator.comparing(Snapshot::getUpdateTime)).orElseThrow();
    }


}
