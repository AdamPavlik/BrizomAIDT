package com.brizom.aidt.moderatorservice.dto;

import lombok.Data;

@Data
public class Signal {

    private String coin;
    private Action action;
    private String reason;
    private int confidence;

}
