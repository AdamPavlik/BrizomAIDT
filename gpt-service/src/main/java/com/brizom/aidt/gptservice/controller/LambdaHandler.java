package com.brizom.aidt.gptservice.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brizom.aidt.gptservice.GptServiceApplication;
import com.brizom.aidt.gptservice.dto.LambdaRequest;
import com.brizom.aidt.gptservice.dto.LambdaResponse;
import com.brizom.aidt.gptservice.service.GPTService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class LambdaHandler implements RequestHandler<LambdaRequest, LambdaResponse> {

    private static final ApplicationContext context = SpringApplication.run(GptServiceApplication.class);
    private final GPTService gptService;

    public LambdaHandler() {
        this.gptService = context.getBean(GPTService.class);
    }

    @Override
    public LambdaResponse handleRequest(LambdaRequest lambdaRequest, Context context) {
        return LambdaResponse.builder().status("SUCCESS").data(gptService.generateSignals()).build();
    }
}
