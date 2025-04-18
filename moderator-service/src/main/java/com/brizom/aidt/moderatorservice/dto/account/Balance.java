package com.brizom.aidt.moderatorservice.dto.account;

import lombok.Data;

@Data
public class Balance {

    private String asset;
    private Double free;
    private Double locked;

}
