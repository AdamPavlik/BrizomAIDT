package com.brizom.aidt.moderatorservice.dto.account;

import lombok.Data;

@Data
public class Snapshot {

    private String type;
    private Long updateTime;
    private SnapshotData data;

}
