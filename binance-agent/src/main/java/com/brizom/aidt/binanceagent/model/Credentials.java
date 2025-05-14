package com.brizom.aidt.binanceagent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {

    private String userId;
    private String binanceKey;
    private String binanceSecretKey;

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("binanceKey")
    public String getBinanceKey() {
        return binanceKey;
    }

    @DynamoDbAttribute("binanceSecretKey")
    public String getBinanceSecretKey() {
        return binanceSecretKey;
    }
}
