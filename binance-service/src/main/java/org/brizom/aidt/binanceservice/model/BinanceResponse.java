package org.brizom.aidt.binanceservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BinanceResponse {

    private String code;
    private String msg;

}
