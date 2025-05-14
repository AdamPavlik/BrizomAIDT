package com.brizom.aidt.moderatorservice.service;

import com.brizom.aidt.moderatorservice.dto.account.AccountSnapshot;
import com.brizom.aidt.moderatorservice.dto.exchange.ExchangeInfo;
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

    @Value("${aws.lambda.account.snapshot.name}")
    private String accountSnapshotFunction;

    @Value("${aws.lambda.exchange.info.name}")
    private String exchangeInfoFunction;

    private final LambdaClient lambdaClient;
    private final Gson gson;

    public AccountSnapshot getAccountSnapshot(String userId) {
        val invokeRequest = InvokeRequest.builder()
                .functionName(accountSnapshotFunction)
                .payload(SdkBytes.fromUtf8String(gson.toJson(userId)))
                .build();
        val invokeResponse = lambdaClient.invoke(invokeRequest);
        return gson.fromJson(invokeResponse.payload().asUtf8String(), AccountSnapshot.class);
    }

    public ExchangeInfo exchangeInfo(List<String> symbols) {
        val invokeRequest = InvokeRequest.builder()
                .functionName(exchangeInfoFunction)
                .payload(SdkBytes.fromUtf8String(gson.toJson(symbols)))
                .build();
        val invokeResponse = lambdaClient.invoke(invokeRequest);
        return gson.fromJson(invokeResponse.payload().asUtf8String(), ExchangeInfo.class);
    }


}
