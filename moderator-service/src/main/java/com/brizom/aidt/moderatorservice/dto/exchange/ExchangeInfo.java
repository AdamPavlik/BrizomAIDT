package com.brizom.aidt.moderatorservice.dto.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeInfo {
    public String timezone;
    public long serverTime;
    public ArrayList<RateLimit> rateLimits;
    public ArrayList<Object> exchangeFilters;
    public ArrayList<Symbol> symbols;

}
