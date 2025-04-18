package com.brizom.aidt.moderatorservice.dto.account;

import lombok.Data;

import java.util.List;

@Data
public class AccountSnapshot {

    private List<Snapshot> snapshotVos;

}
