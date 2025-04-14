package com.brizom.aidt.gptservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Configuration
public class DynamoDBSchemaConfig {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDBSchemaConfig(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @PostConstruct
    public void validateSchema() {
        validatePromptTable();
        validateCoinTable();
    }

    private void validatePromptTable() {
        try {
            dynamoDbClient.describeTable(builder -> builder.tableName("Prompt").build());
        } catch (ResourceNotFoundException e) {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName("Prompt")
                    .tags(Tag.builder().key("BrizomAIDT").value("BrizomAIDT-PromptTable").build())
                    .keySchema(KeySchemaElement.builder().attributeName("id").keyType("HASH").build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType("S").build())
                    .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build())
                    .build();
            dynamoDbClient.createTable(createTableRequest);
        }
    }

    private void validateCoinTable() {
        try {
            dynamoDbClient.describeTable(builder -> builder.tableName("Coin").build());
        } catch (ResourceNotFoundException e) {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName("Coin")
                    .tags(Tag.builder().key("BrizomAIDT").value("BrizomAIDT-CoinTable").build())
                    .keySchema(KeySchemaElement.builder().attributeName("id").keyType("HASH").build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType("S").build())
                    .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build())
                    .build();
            dynamoDbClient.createTable(createTableRequest);
        }
    }


}
