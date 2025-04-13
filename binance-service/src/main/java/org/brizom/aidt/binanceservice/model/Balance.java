package org.brizom.aidt.binanceservice.model;

import lombok.Data;

@Data
public class Balance {

    private String asset;
    private Double free;
    private Double locked;

}
