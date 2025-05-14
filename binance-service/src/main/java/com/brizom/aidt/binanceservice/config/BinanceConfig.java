package com.brizom.aidt.binanceservice.config;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceConfig {

    @Bean
    public SpotClient spotClient() {
        return new SpotClientImpl(DefaultUrls.PROD_URL);
    }

}
