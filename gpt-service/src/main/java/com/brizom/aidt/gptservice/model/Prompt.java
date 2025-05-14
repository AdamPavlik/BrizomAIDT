package com.brizom.aidt.gptservice.model;

import com.brizom.aidt.gptservice.model.enums.PromptRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Data
@DynamoDbBean
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prompt {

    private String id;
    private String userId;
    private String prompt;
    private PromptRole role;
    private boolean enabled;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("prompt")
    public String getPrompt() {
        return prompt;
    }

    @DynamoDbAttribute("role")
    public PromptRole getRole() {
        return role;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "userId-index")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("enabled")
    public boolean isEnabled() {
        return enabled;
    }
}
