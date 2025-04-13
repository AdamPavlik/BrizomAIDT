package org.brizom.aidt.gptservice.dto;

import lombok.Data;
import org.brizom.aidt.gptservice.dto.enums.Action;

@Data
public class Signal {

    private String coin;
    private Action action;
    private String reason;
    private int confidence;

}
