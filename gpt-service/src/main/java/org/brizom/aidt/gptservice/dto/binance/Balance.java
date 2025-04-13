package org.brizom.aidt.gptservice.dto.binance;

import lombok.Data;

@Data
public class Balance {

    private String asset;
    private Double free;
    private Double locked;

}
