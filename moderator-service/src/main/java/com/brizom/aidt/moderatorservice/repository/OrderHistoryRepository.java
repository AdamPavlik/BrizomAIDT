package com.brizom.aidt.moderatorservice.repository;

import com.brizom.aidt.moderatorservice.dto.Order;
import com.brizom.aidt.moderatorservice.model.OrderHistory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
public class OrderHistoryRepository {

    private final DynamoDbTable<OrderHistory> orderHistoryTable;

    public OrderHistoryRepository(DynamoDbEnhancedClient client) {
        this.orderHistoryTable = client.table("Orders", TableSchema.fromBean(OrderHistory.class));
    }

    public void storeOrders(List<Order> orders, String userId) {
        orders.stream().map(order -> new OrderHistory(order, userId)).forEach(orderHistoryTable::putItem);
    }

}
