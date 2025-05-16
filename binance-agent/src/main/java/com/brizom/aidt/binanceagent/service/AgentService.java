package com.brizom.aidt.binanceagent.service;


import com.binance.connector.client.SpotClient;
import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.utils.signaturegenerator.HmacSignatureGenerator;
import com.brizom.aidt.binanceagent.dto.OrderEvent;
import com.brizom.aidt.binanceagent.dto.OrderSide;
import com.brizom.aidt.binanceagent.dto.OrderType;
import com.brizom.aidt.binanceagent.model.Credentials;
import com.brizom.aidt.binanceagent.repository.CredentialsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AgentService {

    private final CredentialsRepository credentialsRepository;

    public void newMarketOrder(OrderEvent orderEvent) {
        log.info("New market order event: {}", orderEvent);
        if (!orderEvent.getSetting().isExecuteOrders()) {
            log.info("New market order event ignored: {}", orderEvent);
            return;
        }
        SpotClient client = initPrivateClient(orderEvent.getSetting().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No credentials found for user " + orderEvent.getSetting().getUserId()));
        orderEvent.getOrders().forEach(order -> {
            try {
                log.info("New market order: {}", order);
                Map<String, Object> parameters = new LinkedHashMap<>();
                parameters.put("symbol", order.getSymbol());
                parameters.put("side", order.getSide().name());
                parameters.put("type", order.getType().name());
                // Handle MARKET orders correctly
                if (order.getType() == OrderType.MARKET) {
                    if (order.getSide() == OrderSide.BUY) {
                        if (order.getQuoteOrderQty() == null) {
                            log.warn("Market BUY order missing quoteOrderQty, using quantity instead");
                            throw new IllegalArgumentException("Market BUY order requires quoteOrderQty");
                        }
                        parameters.put("quoteOrderQty", order.getQuoteOrderQty());
                    } else {
                        if (order.getQuantity() == null) {
                            log.warn("Market SELL order missing quantity, using quantity instead");
                            throw new IllegalArgumentException("Market SELL order requires quantity");
                        }
                        parameters.put("quantity", order.getQuantity());
                    }
                } else {
                    // For non-market orders, use quantity
                    log.info("Using quantity for non-market order: {}", order);
                    parameters.put("quantity", order.getQuantity());
                }
                log.info("New market order parameters: {}", parameters);
                String result = client.createTrade().newOrder(parameters);
                log.info("New market order result: {}", result);
            } catch (Exception ex) {
                log.error("Failed to execute order, event: {}, messageL {}", orderEvent, ex.getMessage());
            }
        });


    }

    private Optional<SpotClient> initPrivateClient(String userId) {
        log.info("Initializing private client for user {}", userId);
        Optional<Credentials> credentials = credentialsRepository.queryByUserId(userId);
        if (credentials.isPresent()) {
            HmacSignatureGenerator signGenerator = new HmacSignatureGenerator(credentials.get().getBinanceSecretKey());
            return Optional.of(new SpotClientImpl(credentials.get().getBinanceKey(), signGenerator, DefaultUrls.PROD_URL));
        }
        return Optional.empty();
    }


}
