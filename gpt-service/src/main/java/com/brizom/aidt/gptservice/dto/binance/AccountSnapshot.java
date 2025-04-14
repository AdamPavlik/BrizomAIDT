package com.brizom.aidt.gptservice.dto.binance;

import lombok.Data;

import java.util.List;

@Data
public class AccountSnapshot {

    private List<Snapshot> snapshotVos;
    
}
