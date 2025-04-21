package com.brizom.aidt.gptservice.service;

import com.brizom.aidt.gptservice.dto.Signals;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesService {

    @Value("${aws.ses.signals.from.address}")
    private String fromAddress;

    @Value("${aws.ses.signals.to.address}")
    private List<String> toAddresses;

    @Value("${aws.ses.signals.template}")
    private String template;

    public final SesClient ses;
    public final Gson gson;


    public void sendSignalsEmails(Signals signals) {
        val request = SendTemplatedEmailRequest.builder()
                .source(fromAddress)
                .destination(Destination.builder().toAddresses(toAddresses).build())
                .template(template)
                .templateData(gson.toJson(signals))
                .build();

        ses.sendTemplatedEmail(request);
    }
}
