package com.brizom.aidt.gptservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Bean
    @Primary
    @Profile("!local")
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create()) // uses default credential chain
                .build();
    }

    @Bean
    @Profile("local")
    public DynamoDbClient amazonDynamoDBLocal() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:9000/"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("key", "key2")))
                .build();
    }

    @Bean
    @Primary
    @Profile("!local")
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }

    @Bean
    @Profile("local")
    public DynamoDbEnhancedClient dynamoDbEnhancedClientLocal() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(amazonDynamoDBLocal())
                .build();
    }

}
