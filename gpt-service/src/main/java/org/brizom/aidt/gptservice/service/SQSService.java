package org.brizom.aidt.gptservice.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.brizom.aidt.gptservice.dto.Signals;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.UUID;

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
        val entries = signals.getSignals().stream()
                .map(gson::toJson)
                .map(signal -> SendMessageBatchRequestEntry.builder().id(UUID.randomUUID().toString()).messageBody(signal).build())
                .toList();

        val sendMessageBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(signalsQueueUrl)
                .entries(entries)
                .build();

        val sendMessageBatchResponse = sqsClient.sendMessageBatch(sendMessageBatchRequest);
        log.info("SQS batch result - Success: {}, Failures: {}", sendMessageBatchResponse.successful(), sendMessageBatchResponse.failed());
    }


}
