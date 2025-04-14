package com.brizom.aidt.gptservice.repository;

import com.brizom.aidt.gptservice.model.Coin;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
public class CoinRepository {

    private final DynamoDbTable<Coin> coinTable;

    public CoinRepository(DynamoDbEnhancedClient client) {
        this.coinTable = client.table("Coin", TableSchema.fromBean(Coin.class));
    }

    public List<Coin> findAll() {
        return coinTable.scan().items().stream().toList();
    }

}
