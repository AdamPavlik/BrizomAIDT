package com.brizom.aidt.gptservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class Signals {

    private Setting setting;
    private List<Coin> coins;
    private List<Signal> signals;

}
