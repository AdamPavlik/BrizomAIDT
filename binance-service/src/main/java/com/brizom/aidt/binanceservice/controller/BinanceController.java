package com.brizom.aidt.binanceservice.controller;

import com.brizom.aidt.binanceservice.model.AccountSnapshot;
import com.brizom.aidt.binanceservice.model.Ticker24HWrapper;
import com.brizom.aidt.binanceservice.service.BinanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/v1/binance")
@AllArgsConstructor
public class BinanceController {

    private final BinanceService binanceService;

    @GetMapping("/getAccountSnapshot")
    public ResponseEntity<AccountSnapshot> getAccountSnapshot() throws JsonProcessingException {
        return ResponseEntity.ok(binanceService.getAccountSnapshot());
    }

    @GetMapping("/getTicker24H")
    public ResponseEntity<Ticker24HWrapper> getTicker24H(@RequestParam("symbols") List<String> symbols) throws JsonProcessingException {
        return ResponseEntity.ok(binanceService.getTicker24H(symbols));
    }

}
