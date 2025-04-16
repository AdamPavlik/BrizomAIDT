package com.brizom.aidt.moderatorservice.service;

import com.brizom.aidt.moderatorservice.dto.Action;
import com.brizom.aidt.moderatorservice.dto.Order;
import com.brizom.aidt.moderatorservice.dto.OrderSide;
import com.brizom.aidt.moderatorservice.dto.Signal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessingService {

    public Optional<Order> processSignal(Signal signal) {
        if (signal.getAction() == Action.HOLD) {
            return Optional.empty();
        }
        //TODO implement logic
        return Optional.of(Order.builder().side(OrderSide.SELL).quoteOrderQty(1.0).build());
    }


}
