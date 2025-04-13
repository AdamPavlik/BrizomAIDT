package org.brizom.aidt.binanceagent.service;


import com.binance.connector.client.SpotClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brizom.aidt.binanceagent.dto.Order;
import org.brizom.aidt.binanceagent.dto.OrderSide;
import org.brizom.aidt.binanceagent.dto.OrderType;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AgentService {

    private final SpotClient client;


    public void init() {
        Order sell = Order.builder().type(OrderType.MARKET).symbol("BNBUSDT").side(OrderSide.SELL).quantity(0.01).build();   //  BNB amount to sell
        Order buy = Order.builder().type(OrderType.MARKET).symbol("BNBUSDT").side(OrderSide.BUY).quoteOrderQty(0.01).build();   // USDT spend to buy
    }

    public void newMarketOrder(Order order) {
        log.info("New market order: {}", order);
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", order.getSymbol());
        parameters.put("side", order.getSide().name());
        parameters.put("type", order.getType().name());
        // Handle MARKET orders correctly
        if (order.getType() == OrderType.MARKET) {
            if (order.getSide() == OrderSide.BUY) {
                if (order.getQuoteOrderQty() == null) {
                    throw new IllegalArgumentException("Market BUY order requires quoteOrderQty");
                }
                parameters.put("quoteOrderQty", order.getQuoteOrderQty());
            } else {
                if (order.getQuantity() == null) {
                    throw new IllegalArgumentException("Market SELL order requires quantity");
                }
                parameters.put("quantity", order.getQuantity());
            }
        } else {
            // For non-market orders, use quantity
            parameters.put("quantity", order.getQuantity());
        }

        String result = client.createTrade().testNewOrder(parameters);
        log.info("New market order result: {}", result);
    }


}
