package com.brizom.aidt.binanceagent.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

    String symbol;
    OrderSide side;
    OrderType type;
    Double quantity;
    Double quoteOrderQty;

}
