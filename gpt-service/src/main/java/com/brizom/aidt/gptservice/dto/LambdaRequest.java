package com.brizom.aidt.gptservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaRequest {
    private String action;
    private Map<String, Object> parameters;
}
