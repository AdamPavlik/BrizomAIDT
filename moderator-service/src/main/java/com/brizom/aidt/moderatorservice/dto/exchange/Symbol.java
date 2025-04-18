package com.brizom.aidt.moderatorservice.dto.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Symbol {
    public String symbol;
    public String status;
    public String baseAsset;
    public int baseAssetPrecision;
    public String quoteAsset;
    public int quotePrecision;
    public int quoteAssetPrecision;
    public int baseCommissionPrecision;
    public int quoteCommissionPrecision;
    public ArrayList<String> orderTypes;
    public boolean icebergAllowed;
    public boolean ocoAllowed;
    public boolean otoAllowed;
    public boolean quoteOrderQtyMarketAllowed;
    public boolean allowTrailingStop;
    public boolean cancelReplaceAllowed;
    public boolean isSpotTradingAllowed;
    public boolean isMarginTradingAllowed;
    public ArrayList<Filter> filters;
    public ArrayList<Object> permissions;
    public ArrayList<ArrayList<String>> permissionSets;
    public String defaultSelfTradePreventionMode;
    public ArrayList<String> allowedSelfTradePreventionModes;

}
