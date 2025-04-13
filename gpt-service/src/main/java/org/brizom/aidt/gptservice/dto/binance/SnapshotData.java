package org.brizom.aidt.gptservice.dto.binance;

import lombok.Data;

import java.util.List;

@Data
public class SnapshotData {

    private Double totalAssetOfBtc;
    private List<Balance> balances;

}
