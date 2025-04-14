package com.brizom.aidt.binanceservice.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brizom.aidt.binanceservice.BinanceServiceApplication;
import com.brizom.aidt.binanceservice.dto.LambdaRequest;
import com.brizom.aidt.binanceservice.dto.LambdaResponse;
import com.brizom.aidt.binanceservice.dto.Status;
import com.brizom.aidt.binanceservice.service.BinanceService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class LambdaHandler implements RequestHandler<LambdaRequest, LambdaResponse> {

    private static final ApplicationContext context = SpringApplication.run(BinanceServiceApplication.class);
    private final BinanceService binanceService;

    public LambdaHandler() {
        this.binanceService = context.getBean(BinanceService.class);
    }

    @Override
    public LambdaResponse handleRequest(LambdaRequest lambdaRequest, Context context) {
        return switch (lambdaRequest.getAction()) {
            case "getAccountSnapshot" ->
                    LambdaResponse.builder().status(Status.SUCCESS.name()).data(binanceService.getAccountSnapshot()).build();
            case "getTicker24H" ->
                    LambdaResponse.builder().status(Status.SUCCESS.name()).data(binanceService.getTicker24H((List<String>) lambdaRequest.getParameters().get("symbols"))).build();
            default ->
                    LambdaResponse.builder().status(Status.ERROR.name()).data("Unknown action: " + lambdaRequest.getAction()).build();
        };
    }
}
