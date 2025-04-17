package com.brizom.aidt.binanceservice.handler;

import com.brizom.aidt.binanceservice.model.AccountSnapshot;
import com.brizom.aidt.binanceservice.model.Ticker24HWrapper;
import com.brizom.aidt.binanceservice.service.BinanceService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@AllArgsConstructor
public class LambdaHandler {

    private final BinanceService binanceService;

    @Bean
    public Supplier<AccountSnapshot> accountSnapshot() {
        return binanceService::getAccountSnapshot;
    }

    @Bean
    public Function<List<String>, Ticker24HWrapper> ticker24H() {
        return binanceService::getTicker24H;
    }

}
