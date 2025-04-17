package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.binance.AccountSnapshot;
import com.brizom.aidt.gptservice.dto.binance.Ticker24HWrapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceService {

    @Value("${aws.lambda.ticker24H.name}")
    private String ticker24HFunction;

    @Value("${aws.lambda.account.snapshot.name}")
    private String accountSnapshotFunction;

    private final LambdaClient lambdaClient;
    private final Gson gson;

    public AccountSnapshot getAccountSnapshot() {
        val invokeRequest = InvokeRequest.builder()
                .functionName(accountSnapshotFunction)
                .build();
        val invokeResponse = lambdaClient.invoke(invokeRequest);
        return gson.fromJson(invokeResponse.payload().asUtf8String(), AccountSnapshot.class);
    }

    public Ticker24HWrapper getTicker24H(List<String> symbols) {
        val invokeRequest = InvokeRequest.builder()
                .functionName(ticker24HFunction)
                .payload(SdkBytes.fromUtf8String(gson.toJson(symbols)))
                .build();
        val invokeResponse = lambdaClient.invoke(invokeRequest);
        return gson.fromJson(invokeResponse.payload().asUtf8String(), Ticker24HWrapper.class);
    }

}
