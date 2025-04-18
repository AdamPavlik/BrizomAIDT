package com.brizom.aidt.moderatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Signal {

    private String coin;
    private Action action;
    private String reason;
    private int confidence;

}
