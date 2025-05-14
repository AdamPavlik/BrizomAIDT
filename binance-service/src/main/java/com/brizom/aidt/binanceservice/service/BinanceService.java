package com.brizom.aidt.binanceservice.service;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Wallet;
import com.binance.connector.client.utils.signaturegenerator.HmacSignatureGenerator;
import com.brizom.aidt.binanceservice.model.AccountSnapshot;
import com.brizom.aidt.binanceservice.model.Credentials;
import com.brizom.aidt.binanceservice.model.Ticker24H;
import com.brizom.aidt.binanceservice.model.Ticker24HWrapper;
import com.brizom.aidt.binanceservice.model.exchange.ExchangeInfo;
import com.brizom.aidt.binanceservice.repository.CredentialsRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BinanceService {

    private final SpotClient publicSpotClient;
    private final CredentialsRepository credentialsRepository;
    private final Gson gson;

    public AccountSnapshot getAccountSnapshot(String userId) {
        Optional<SpotClient> privateClient = initPrivateClient(userId);

        if (privateClient.isPresent()) {
            Wallet wallet = privateClient.get().createWallet();
            HashMap<String, Object> params = new HashMap<>();
            params.put("type", "SPOT");
            return gson.fromJson(wallet.accountSnapshot(params), AccountSnapshot.class);
        }
        return new AccountSnapshot();
    }

    public Ticker24HWrapper getTicker24H(List<String> symbols) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", "FULL");
        params.put("symbols", new ArrayList<>(symbols));
        return Ticker24HWrapper.builder().ticker24H(gson.fromJson(publicSpotClient.createMarket().ticker24H(params), new TypeToken<List<Ticker24H>>() {
                }))
                .build();
    }

    public ExchangeInfo exchangeInfo(List<String> symbols) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbols", new ArrayList<>(symbols));
        return gson.fromJson(publicSpotClient.createMarket().exchangeInfo(params), ExchangeInfo.class);
    }

    private Optional<SpotClient> initPrivateClient(String userId) {
        Optional<Credentials> credentials = credentialsRepository.queryByUserId(userId);
        if (credentials.isPresent()) {
            HmacSignatureGenerator signGenerator = new HmacSignatureGenerator(credentials.get().getBinanceSecretKey());
            return Optional.of(new SpotClientImpl(credentials.get().getBinanceKey(), signGenerator, DefaultUrls.PROD_URL));
        }
        return Optional.empty();
    }
}
