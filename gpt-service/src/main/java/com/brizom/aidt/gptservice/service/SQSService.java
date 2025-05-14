package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.Signals;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@Service
@RequiredArgsConstructor
@Slf4j
public class SQSService {

    @Value("${aws.sqs.queue.name.signals}")
    private String signalsQueueUrl;

    private final SqsClient sqsClient;
    private final Gson gson;

    public void sendSignalMessages(Signals signals) {
        log.info("Sending a signal message to SQS: {}", signals);
        val request = SendMessageRequest.builder()
                .queueUrl(signalsQueueUrl)
                .messageBody(gson.toJson(signals))
                .build();
        val response = sqsClient.sendMessage(request);
        log.info("SQS batch result - {}}", response);
    }


}
