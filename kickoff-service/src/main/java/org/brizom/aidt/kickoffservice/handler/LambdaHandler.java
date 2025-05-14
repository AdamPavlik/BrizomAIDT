package org.brizom.aidt.kickoffservice.handler;

import lombok.AllArgsConstructor;
import org.brizom.aidt.kickoffservice.service.KickoffService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@AllArgsConstructor
public class LambdaHandler {

    private final KickoffService service;

    @Bean
    public Supplier<String> kickoff() {
        return service::kickOff;
    }

}
