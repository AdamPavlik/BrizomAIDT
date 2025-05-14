package com.brizom.aidt.moderatorservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Signals {

    private Setting setting;
    private List<Coin> coins;
    private List<Signal> signals;

}
