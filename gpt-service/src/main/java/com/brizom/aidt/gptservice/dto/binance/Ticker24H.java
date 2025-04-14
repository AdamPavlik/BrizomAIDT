package com.brizom.aidt.gptservice.dto.binance;

import lombok.Data;

@Data
public class Ticker24H {
    private String symbol;
    private Double priceChange;
    private Double priceChangePercent;
    private Double weightedAvgPrice;
    private Double prevClosePrice;
    private Double lastPrice;
    private Double lastQty;
    private Double bidPrice;
    private Double bidQty;
    private Double askPrice;
    private Double askQty;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double volume;
    private Double quoteVolume;
    private Long openTime;
    private Long closeTime;
    private Long firstId;
    private Long lastId;
    private Long count;
}
