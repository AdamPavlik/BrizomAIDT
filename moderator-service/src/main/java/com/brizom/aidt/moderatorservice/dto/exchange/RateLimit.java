package com.brizom.aidt.moderatorservice.dto.exchange;

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
