package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.binance.AccountSnapshot;
import com.brizom.aidt.gptservice.dto.binance.Ticker24HWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@AllArgsConstructor
public class BinanceService {

    private final RestClient binanceRestClient;

    public AccountSnapshot getAccountSnapshot() {
        return binanceRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getAccountSnapshot").build())
                .retrieve().body(AccountSnapshot.class);
    }

    public Ticker24HWrapper getTicker24H(List<String> symbols) {
        return binanceRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getTicker24H")
                        .queryParam("symbols", symbols)
                        .build())
                .retrieve().body(Ticker24HWrapper.class);
    }


}
