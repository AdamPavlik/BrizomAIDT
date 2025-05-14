package com.brizom.aidt.gptservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coin {

    private String id;
    private String symbol;
    private String userId;
    private boolean executeOrder;
    private boolean generateSignal;
    private boolean sendEmail;


}
