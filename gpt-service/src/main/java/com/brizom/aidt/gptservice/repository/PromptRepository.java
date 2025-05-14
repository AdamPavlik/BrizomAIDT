package com.brizom.aidt.gptservice.repository;

import com.brizom.aidt.gptservice.model.Prompt;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

@Repository
public class PromptRepository {

    private final DynamoDbTable<Prompt> promptTable;

    public PromptRepository(DynamoDbEnhancedClient client) {
        this.promptTable = client.table("Prompt", TableSchema.fromBean(Prompt.class));
    }

    public List<Prompt> queryByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());
        Expression expression = Expression.builder()
                .expression("#gs = :gs")
                .expressionNames(Map.of("#gs", "enabled"))
                .expressionValues(Map.of(":gs", AttributeValue.builder().bool(true).build()))
                .build();
        QueryEnhancedRequest query = QueryEnhancedRequest.builder().queryConditional(queryConditional).filterExpression(expression).build();
        return promptTable.index("userId-index").query(query).stream().flatMap(promptPage -> promptPage.items().stream()).toList();
    }

}
