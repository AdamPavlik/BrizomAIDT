package com.brizom.aidt.binanceagent.repository;


import com.brizom.aidt.binanceagent.model.Credentials;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Optional;

@Repository
public class CredentialsRepository {

    private final DynamoDbTable<Credentials> credentialsTable;

    public CredentialsRepository(DynamoDbEnhancedClient client) {
        this.credentialsTable = client.table("Credentials", TableSchema.fromBean(Credentials.class));
    }

    public Optional<Credentials> queryByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());
        QueryEnhancedRequest query = QueryEnhancedRequest.builder().queryConditional(queryConditional).build();
        return credentialsTable.query(query).stream().flatMap(credentialsPage -> credentialsPage.items().stream()).findFirst();
    }

}
