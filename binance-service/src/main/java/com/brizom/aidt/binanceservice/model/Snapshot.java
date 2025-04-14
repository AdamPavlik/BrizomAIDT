package com.brizom.aidt.binanceservice.model;

import lombok.Data;

import java.util.Date;

@Data
public class Snapshot {

    private String type;
    private Date updateTime;
    private SnapshotData data;

}
