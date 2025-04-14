package com.brizom.aidt.gptservice.dto.binance;

import lombok.Data;

import java.util.Date;

@Data
public class Snapshot {

    private String type;
    private Date updateTime;
    private SnapshotData data;

}
