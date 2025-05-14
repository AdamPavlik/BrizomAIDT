package com.brizom.aidt.moderatorservice.service;

import com.brizom.aidt.moderatorservice.dto.Signal;
import com.brizom.aidt.moderatorservice.dto.Signals;
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

    @Value("${aws.ses.signals.template}")
    private String template;

    public final SesClient ses;
    public final Gson gson;

    public void sendSignalsEmails(List<Signal> signals, String... toAddresses) {
        val request = SendTemplatedEmailRequest.builder()
                .source(fromAddress)
                .destination(Destination.builder().toAddresses(toAddresses).build())
                .template(template)
                .templateData(gson.toJson(Signals.builder().signals(signals).build()))
                .build();
        ses.sendTemplatedEmail(request);
    }
}
