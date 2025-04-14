package com.brizom.aidt.binanceservice.service;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.spot.Wallet;
import com.brizom.aidt.binanceservice.model.AccountSnapshot;
import com.brizom.aidt.binanceservice.model.Ticker24H;
import com.brizom.aidt.binanceservice.model.Ticker24HWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class BinanceService {

    private final SpotClient spotClient;
    private final ObjectMapper objectMapper;

    public AccountSnapshot getAccountSnapshot() {
        Wallet wallet = spotClient.createWallet();
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", "SPOT");
        try {
            return objectMapper.readValue(wallet.accountSnapshot(params), AccountSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Ticker24HWrapper getTicker24H(List<String> symbols) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", "FULL");
        params.put("symbols", symbols);
        try {
            return Ticker24HWrapper.builder().ticker24H(objectMapper.readValue(spotClient.createMarket().ticker24H(params), new TypeReference<List<Ticker24H>>() {
            })).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
