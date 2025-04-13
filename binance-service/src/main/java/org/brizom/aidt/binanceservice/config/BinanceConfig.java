package org.brizom.aidt.binanceservice.config;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BinanceConfig {

    public static final String BASE_URL = "https://testnet.binance.vision";

    public static final String API_KEY = System.getenv("API_KEY");
    public static final String SECRET_KEY = System.getenv("SECRET_KEY");

    @Bean
    @Profile( "local")
    public SpotClient spotClientLocal() {
        return new SpotClientImpl(BASE_URL);
    }

    @Bean
    @Profile( "!local")
    public SpotClient spotClient() {
        return new SpotClientImpl(API_KEY, SECRET_KEY);
    }

}
