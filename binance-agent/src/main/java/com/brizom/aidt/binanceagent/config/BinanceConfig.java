package com.brizom.aidt.binanceagent.config;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.utils.signaturegenerator.HmacSignatureGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceConfig {

    public static final String API_KEY = System.getenv("API_KEY");
    public static final String SECRET_KEY = System.getenv("SECRET_KEY");

    @Bean
    public SpotClient spotClient() {
        HmacSignatureGenerator signGenerator = new HmacSignatureGenerator(SECRET_KEY);
        return new SpotClientImpl(API_KEY, signGenerator, DefaultUrls.PROD_URL);
    }

}
