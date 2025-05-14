package com.brizom.aidt.moderatorservice.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderEvent {

    private Setting setting;
    private List<Order> orders;


}
