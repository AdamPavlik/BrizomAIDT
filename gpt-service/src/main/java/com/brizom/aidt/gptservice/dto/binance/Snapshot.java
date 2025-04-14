package com.brizom.aidt.gptservice.dto.binance;

import lombok.Data;

@Data
public class Snapshot {

    private String type;
    private Long updateTime;
    private SnapshotData data;

}
