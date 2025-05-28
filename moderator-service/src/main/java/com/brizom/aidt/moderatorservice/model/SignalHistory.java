package com.brizom.aidt.moderatorservice.model;

import com.brizom.aidt.moderatorservice.dto.Action;
import com.brizom.aidt.moderatorservice.dto.Signal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@DynamoDbBean
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignalHistory {

    private String id;
    private String userId;
    private String coin;
    private Action action;
    private String reason;
    private int confidence;
    private ZonedDateTime date;
    private long timestamp;

    public SignalHistory(Signal signal, String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.coin = signal.getCoin();
        this.action = signal.getAction();
        this.reason = signal.getReason();
        this.confidence = signal.getConfidence();
        this.date = ZonedDateTime.now(ZoneOffset.UTC);
        this.timestamp = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
    }


    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("coin")
    public String getCoin() {
        return coin;
    }

    @DynamoDbAttribute("action")
    public Action getAction() {
        return action;
    }

    @DynamoDbAttribute("reason")
    public String getReason() {
        return reason;
    }

    @DynamoDbAttribute("confidence")
    public int getConfidence() {
        return confidence;
    }

    @DynamoDbAttribute("date")
    public ZonedDateTime getDate() {
        return date;
    }

    @DynamoDbAttribute("timestamp")
    public long getTimestamp() {
        return timestamp;
    }
}
