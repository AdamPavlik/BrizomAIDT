package org.brizom.aidt.gptservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api.service.url.binance-service}")
    private String baseUrl;

    @Bean
    public RestClient binanceRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


}
