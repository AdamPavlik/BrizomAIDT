package com.brizom.aidt.binanceservice.model;

import lombok.Data;

@Data
public class Snapshot {

    private String type;
    private Long updateTime;
    private SnapshotData data;

}
