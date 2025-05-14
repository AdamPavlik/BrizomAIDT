package com.brizom.aidt.moderatorservice.repository;

import com.brizom.aidt.moderatorservice.dto.Signal;
import com.brizom.aidt.moderatorservice.model.SignalHistory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
public class SignalHistoryRepository {

    private final DynamoDbTable<SignalHistory> signalHistoryTable;

    public SignalHistoryRepository(DynamoDbEnhancedClient client) {
        this.signalHistoryTable = client.table("Signals", TableSchema.fromBean(SignalHistory.class));
    }

    public void storeSignals(List<Signal> signals, String userId) {
        signals.stream().map(signal -> new SignalHistory(signal, userId)).forEach(signalHistoryTable::putItem);
    }


}
