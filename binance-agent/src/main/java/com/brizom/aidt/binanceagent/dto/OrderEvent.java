package com.brizom.aidt.binanceagent.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderEvent {

    private Setting setting;
    private List<Order> orders;


}
