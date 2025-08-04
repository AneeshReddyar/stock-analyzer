package com.revstox.stock_analyzer.controller;

import com.revstox.stock_analyzer.model.DailyPrice;
import com.revstox.stock_analyzer.model.Stock;
import com.revstox.stock_analyzer.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    
    @Autowired
    private StockService stockService;
    
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        logger.info("GET request received for all stocks");
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }
    
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        logger.info("GET request received for stock: {}", symbol);
        Stock stock = stockService.getStockBySymbol(symbol);
        
        if (stock == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(stock);
    }
    
    @GetMapping("/{symbol}/prices")
    public ResponseEntity<List<DailyPrice>> getStockPrices(@PathVariable String symbol) {
        logger.info("GET request received for stock prices: {}", symbol);
        List<DailyPrice> prices = stockService.getStockPrices(symbol);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/{symbol}/prices/range")
    public ResponseEntity<List<DailyPrice>> getStockPricesInRange(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request received for stock prices: {} from {} to {}", symbol, startDate, endDate);
        List<DailyPrice> prices = stockService.getStockPricesInRange(symbol, startDate, endDate);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/{symbol}/prices/{date}")
    public ResponseEntity<DailyPrice> getStockPriceOnDate(
            @PathVariable String symbol,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("GET request received for stock price: {} on {}", symbol, date);
        DailyPrice price = stockService.getStockPriceOnDate(symbol, date);
        
        if (price == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(price);
    }
}
