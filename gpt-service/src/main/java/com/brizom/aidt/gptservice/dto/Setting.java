package com.brizom.aidt.gptservice.dto;

import com.brizom.aidt.gptservice.dto.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
    private String userId;
    private boolean sendEmails;
    private boolean generateSignals;
    private boolean executeOrders;
    private AIProvider aiProvider;
    private AIModel aiModel;
    private Effort effort;
    private int maxTokens;
    private int startTime;
    private String email;
    private StableCoin stableCoin;
    private int balanceUtilization;
    private Action onHoldAction;
    private int confidenceToBuy;
    private int confidenceToSell;
    private boolean includeBalances;
    private boolean includeLiveData;
}