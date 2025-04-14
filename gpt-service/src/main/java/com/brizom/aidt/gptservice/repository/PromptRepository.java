package com.brizom.aidt.gptservice.repository;

import com.brizom.aidt.gptservice.model.Prompt;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
public class PromptRepository {

    private final DynamoDbTable<Prompt> promptTable;

    public PromptRepository(DynamoDbEnhancedClient client) {
        this.promptTable = client.table("Prompt", TableSchema.fromBean(Prompt.class));
    }

    public List<Prompt> findAll() {
        return promptTable.scan().items().stream().toList();
    }
}
