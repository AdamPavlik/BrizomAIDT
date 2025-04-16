package com.brizom.aidt.moderatorservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    String symbol;
    OrderSide side;
    OrderType type;
    Double quantity;
    Double quoteOrderQty;

}
