package org.brizom.aidt.kickoffservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Coin {

    private String id;
    private String symbol;
    private String userId;
    private boolean executeOrder;
    private boolean generateSignal;
    private boolean sendEmail;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("symbol")
    public String getSymbol() {
        return symbol;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "userId-index")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("executeOrder")
    public boolean isExecuteOrder() {
        return executeOrder;
    }

    @DynamoDbAttribute("generateSignal")
    public boolean isGenerateSignal() {
        return generateSignal;
    }

    @DynamoDbAttribute("sendEmail")
    public boolean isSendEmail() {
        return sendEmail;
    }
}
