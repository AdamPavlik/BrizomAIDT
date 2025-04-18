package com.brizom.aidt.binanceservice.service;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.spot.Wallet;
import com.brizom.aidt.binanceservice.model.AccountSnapshot;
import com.brizom.aidt.binanceservice.model.Ticker24H;
import com.brizom.aidt.binanceservice.model.Ticker24HWrapper;
import com.brizom.aidt.binanceservice.model.exchange.ExchangeInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class BinanceService {

    private final SpotClient spotClient;
    private final Gson gson;

    public AccountSnapshot getAccountSnapshot() {
        Wallet wallet = spotClient.createWallet();
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", "SPOT");
        return gson.fromJson(wallet.accountSnapshot(params), AccountSnapshot.class);
    }

    public Ticker24HWrapper getTicker24H(List<String> symbols) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", "FULL");
        params.put("symbols", symbols);
        return Ticker24HWrapper.builder().ticker24H(gson.fromJson(spotClient.createMarket().ticker24H(params), new TypeToken<List<Ticker24H>>() {
                }))
                .build();
    }

    public ExchangeInfo exchangeInfo(List<String> symbols) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbols", symbols);
        return gson.fromJson(spotClient.createMarket().exchangeInfo(params), ExchangeInfo.class);
    }

}
