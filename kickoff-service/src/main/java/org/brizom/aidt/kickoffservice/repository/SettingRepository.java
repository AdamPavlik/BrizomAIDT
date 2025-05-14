package org.brizom.aidt.kickoffservice.repository;

import org.brizom.aidt.kickoffservice.model.Setting;
import org.brizom.aidt.kickoffservice.utils.TimeUtils;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Repository
public class SettingRepository {

    private final DynamoDbTable<Setting> settingTable;

    public SettingRepository(DynamoDbEnhancedClient client) {
        this.settingTable = client.table("Settings", TableSchema.fromBean(Setting.class));
    }

    public SdkIterable<Page<Setting>> queryKickOffSettings() {
        int startTime = TimeUtils.getMinutesSinceMidnightUTC();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(startTime).build());
        Expression expression = Expression.builder()
                .expression("#gs = :gs")
                .expressionNames(Map.of("#gs", "generateSignals"))
                .expressionValues(Map.of(":gs", AttributeValue.builder().bool(true).build()))
                .build();
        QueryEnhancedRequest query = QueryEnhancedRequest.builder().queryConditional(queryConditional).filterExpression(expression).build();
        return settingTable.index("startTime-index").query(query);
    }

}
