package com.brizom.aidt.binanceservice.model.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateLimit {
    public String rateLimitType;
    public String interval;
    public int intervalNum;
    public int limit;
}
