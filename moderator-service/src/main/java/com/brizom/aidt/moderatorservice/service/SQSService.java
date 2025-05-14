package com.brizom.aidt.moderatorservice.service;

import com.brizom.aidt.moderatorservice.dto.OrderEvent;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
public class SQSService {

    @Value("${aws.sqs.queue.name.orders}")
    private String ordersQueueUrl;

    private final SqsClient sqsClient;
    private final Gson gson;

    public void sendOrder(OrderEvent order) {
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(ordersQueueUrl)
                .messageBody(gson.toJson(order))
                .build());
    }


}
