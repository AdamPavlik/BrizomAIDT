package org.brizom.aidt.gptservice.dto.binance;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Ticker24HWrapper {

    private List<Ticker24H> ticker24H;

}
