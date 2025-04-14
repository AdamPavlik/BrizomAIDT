package com.brizom.aidt.binanceservice.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brizom.aidt.binanceservice.BinanceServiceApplication;
import com.brizom.aidt.binanceservice.dto.BinanceRequest;
import com.brizom.aidt.binanceservice.dto.BinanceResponse;
import com.brizom.aidt.binanceservice.dto.Status;
import com.brizom.aidt.binanceservice.service.BinanceService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class LambdaHandler implements RequestHandler<BinanceRequest, BinanceResponse> {

    private static final ApplicationContext context = SpringApplication.run(BinanceServiceApplication.class);
    private final BinanceService binanceService;

    public LambdaHandler() {
        this.binanceService = context.getBean(BinanceService.class);
    }

    @Override
    public BinanceResponse handleRequest(BinanceRequest binanceRequest, Context context) {
        return switch (binanceRequest.getAction()) {
            case "getAccountSnapshot" ->
                    BinanceResponse.builder().status(Status.SUCCESS.name()).data(binanceService.getAccountSnapshot()).build();
            case "getTicker24H" ->
                    BinanceResponse.builder().status(Status.SUCCESS.name()).data(binanceService.getTicker24H((List<String>) binanceRequest.getParameters().get("symbols"))).build();
            default ->
                    BinanceResponse.builder().status(Status.ERROR.name()).data("Unknown action: " + binanceRequest.getAction()).build();
        };
    }
}
