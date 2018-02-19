package com.bcg.dv.api.controllers;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.bcg.dv.api.bindings.Ohlc;
import com.bcg.dv.services.StockService;

@RestController
@RequestMapping("/stocks")
public class StockController {
  private StockService stockService;

  public StockController(StockService stockService) {
    this.stockService = stockService;
  }

  @RequestMapping(path = "/{stock}/ohlc", method = RequestMethod.GET)
  public ResponseEntity<?> getStockOhlc(@PathVariable("stock") String stock) {
    Optional<Ohlc> optOhlc = stockService.getStockOhlc(stock);

    return optOhlc.map(ohlc -> new ResponseEntity<Ohlc>(ohlc, HttpStatus.OK))
        .orElse(new ResponseEntity(HttpStatus.BAD_REQUEST));
  }
}
