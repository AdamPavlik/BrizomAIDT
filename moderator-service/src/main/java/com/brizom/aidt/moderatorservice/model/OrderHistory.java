package com.brizom.aidt.moderatorservice.model;

import com.brizom.aidt.moderatorservice.dto.Order;
import com.brizom.aidt.moderatorservice.dto.OrderSide;
import com.brizom.aidt.moderatorservice.dto.OrderType;
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
public class OrderHistory {

    private String id;
    private String userId;
    private String symbol;
    private OrderSide side;
    private OrderType type;
    private Double quantity;
    private Double quoteOrderQty;
    private ZonedDateTime date;


    public OrderHistory(Order order, String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.symbol = order.getSymbol();
        this.side = order.getSide();
        this.type = order.getType();
        this.quantity = order.getQuantity();
        this.quoteOrderQty = order.getQuoteOrderQty();
        this.date = ZonedDateTime.now(ZoneOffset.UTC);
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("symbol")
    public String getSymbol() {
        return symbol;
    }

    @DynamoDbAttribute("side")
    public OrderSide getSide() {
        return side;
    }

    @DynamoDbAttribute("type")
    public OrderType getType() {
        return type;
    }

    @DynamoDbAttribute("quantity")
    public Double getQuantity() {
        return quantity;
    }

    @DynamoDbAttribute("quoteOrderQty")
    public Double getQuoteOrderQty() {
        return quoteOrderQty;
    }
}
