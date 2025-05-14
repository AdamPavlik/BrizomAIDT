package org.brizom.aidt.kickoffservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brizom.aidt.kickoffservice.dto.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
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

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("sendEmails")
    public boolean isSendEmails() {
        return sendEmails;
    }

    @DynamoDbAttribute("generateSignals")
    public boolean isGenerateSignals() {
        return generateSignals;
    }

    @DynamoDbAttribute("executeOrders")
    public boolean isExecuteOrders() {
        return executeOrders;
    }

    @DynamoDbAttribute("aiProvider")
    public AIProvider getAiProvider() {
        return aiProvider;
    }

    @DynamoDbAttribute("aiModel")
    public AIModel getAiModel() {
        return aiModel;
    }

    @DynamoDbAttribute("effort")
    public Effort getEffort() {
        return effort;
    }

    @DynamoDbAttribute("maxTokens")
    public int getMaxTokens() {
        return maxTokens;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "startTime-index")
    public int getStartTime() {
        return startTime;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("stableCoin")
    public StableCoin getStableCoin() {
        return stableCoin;
    }

    @DynamoDbAttribute("onHoldAction")
    public Action getOnHoldAction() {
        return onHoldAction;
    }

    @DynamoDbAttribute("confidenceToBuy")
    public int getConfidenceToBuy() {
        return confidenceToBuy;
    }

    @DynamoDbAttribute("confidenceToSell")
    public int getConfidenceToSell() {
        return confidenceToSell;
    }

    @DynamoDbAttribute("includeBalances")
    public boolean isIncludeBalances() {
        return includeBalances;
    }

    @DynamoDbAttribute("includeLiveData")
    public boolean isIncludeLiveData() {
        return includeLiveData;
    }

    @DynamoDbAttribute("balanceUtilization")
    public int getBalanceUtilization() {
        return balanceUtilization;
    }
}
