package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.LambdaRequest;
import com.brizom.aidt.gptservice.dto.LambdaResponse;
import com.brizom.aidt.gptservice.dto.binance.AccountSnapshot;
import com.brizom.aidt.gptservice.dto.binance.Ticker24HWrapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class BinanceService {

    private final LambdaClient lambdaClient;
    private final Gson gson;

    public AccountSnapshot getAccountSnapshot() {
        val invokeRequest = InvokeRequest.builder()
                .functionName("brizomAIDT-binanceService")
                .payload(SdkBytes.fromUtf8String(gson.toJson(LambdaRequest.builder().action("getAccountSnapshot").build())))
                .build();
        val invokeResponse = lambdaClient.invoke(invokeRequest);
        return gson.fromJson(gson.toJson(gson.fromJson(invokeResponse.payload().asUtf8String(), LambdaResponse.class).getData()), AccountSnapshot.class);
    }

    public Ticker24HWrapper getTicker24H(List<String> symbols) {
        val invokeRequest = InvokeRequest.builder()
                .functionName("brizomAIDT-binanceService")
                .payload(SdkBytes.fromUtf8String(gson.toJson(LambdaRequest.builder().action("getTicker24H").parameters(Map.of("symbols", symbols)).build())))
                .build();
        val invokeResponse = lambdaClient.invoke(invokeRequest);
        return gson.fromJson(gson.toJson(gson.fromJson(invokeResponse.payload().asUtf8String(), LambdaResponse.class).getData()), Ticker24HWrapper.class);
    }

}
