package org.brizom.aidt.kickoffservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brizom.aidt.kickoffservice.model.Coin;
import org.brizom.aidt.kickoffservice.model.Setting;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KickoffEvent {
    private Setting setting;
    private List<Coin> coins;
    private Metadata metadata;
}
