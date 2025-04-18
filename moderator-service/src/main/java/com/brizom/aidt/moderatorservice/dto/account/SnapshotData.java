package com.brizom.aidt.moderatorservice.dto.account;

import lombok.Data;

import java.util.List;

@Data
public class SnapshotData {

    private Double totalAssetOfBtc;
    private List<Balance> balances;

}
