package org.brizom.aidt.kickoffservice.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.brizom.aidt.kickoffservice.dto.KickoffEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSService {

    @Value("${aws.sqs.queue.name.gpt}")
    private String gptQueueUrl;

    private final SqsClient sqsClient;
    private final Gson gson;


    public void sendMessage(KickoffEvent event) {
        log.info("Sending a kickoff message to SQS: {}", event);
        val entry = gson.toJson(event);
        val request = SendMessageRequest.builder().queueUrl(gptQueueUrl).messageBody(entry).build();
        val response = sqsClient.sendMessage(request);
        log.info("SQS result - {}", response);
    }

}
