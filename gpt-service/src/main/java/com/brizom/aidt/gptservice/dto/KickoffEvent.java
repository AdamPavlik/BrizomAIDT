package com.brizom.aidt.gptservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KickoffEvent {

    private Setting setting;
    private List<Coin> coins;

}
