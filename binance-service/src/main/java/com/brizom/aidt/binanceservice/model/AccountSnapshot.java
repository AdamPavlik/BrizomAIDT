package com.brizom.aidt.binanceservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AccountSnapshot extends BinanceResponse {

    private List<Snapshot> snapshotVos;
    
}
