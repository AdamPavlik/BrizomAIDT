package com.brizom.aidt.binanceservice.model;

import lombok.Data;

import java.util.List;

@Data
public class AccountSnapshot extends BinanceResponse {

    private List<Snapshot> snapshotVos;
    
}
