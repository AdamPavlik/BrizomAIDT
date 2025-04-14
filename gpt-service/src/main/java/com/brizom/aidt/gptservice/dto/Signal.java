package com.brizom.aidt.gptservice.dto;

import com.brizom.aidt.gptservice.dto.enums.Action;
import lombok.Data;

@Data
public class Signal {

    private String coin;
    private Action action;
    private String reason;
    private int confidence;

}
