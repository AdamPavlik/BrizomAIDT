package org.brizom.aidt.kickoffservice.repository;

import org.brizom.aidt.kickoffservice.model.Coin;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Repository
public class CoinRepository {

    private final DynamoDbTable<Coin> settingTable;

    public CoinRepository(DynamoDbEnhancedClient client) {
        this.settingTable = client.table("Coin", TableSchema.fromBean(Coin.class));
    }

    public SdkIterable<Page<Coin>> queryCoins(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());
        Expression expression = Expression.builder()
                .expression("#gs = :gs")
                .expressionNames(Map.of("#gs", "generateSignal"))
                .expressionValues(Map.of(":gs", AttributeValue.builder().bool(true).build()))
                .build();
        QueryEnhancedRequest query = QueryEnhancedRequest.builder().queryConditional(queryConditional).filterExpression(expression).build();
        return settingTable.index("userId-index").query(query);
    }


}
