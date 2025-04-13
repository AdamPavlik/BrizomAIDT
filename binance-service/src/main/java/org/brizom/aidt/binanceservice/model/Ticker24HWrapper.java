package org.brizom.aidt.binanceservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Ticker24HWrapper {

    private List<Ticker24H> ticker24H;

}
