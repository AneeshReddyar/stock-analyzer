package com.revstox.stock_analyzer.controller;

import com.revstox.stock_analyzer.service.PriceAnalysisService;
import com.revstox.stock_analyzer.service.VolumeAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private PriceAnalysisService priceAnalysisService;
    
    @Autowired
    private VolumeAnalysisService volumeAnalysisService;
    
    @GetMapping("/price/{symbol}/volatility")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getVolatility(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for volatility: {} from {} to {}", symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> volatility = priceAnalysisService.getVolatilityForPeriod(symbol, startDate, endDate);
        return ResponseEntity.ok(volatility);
    }
    
    @GetMapping("/price/{symbol}/changes")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getPriceChanges(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for price changes: {} from {} to {}", symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> priceChanges = priceAnalysisService.getPriceChangesForPeriod(symbol, startDate, endDate);
        return ResponseEntity.ok(priceChanges);
    }
    
    @GetMapping("/price/{symbol}/moving-average/7")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getMovingAverage7Days(@PathVariable String symbol) {
        logger.info("GET request for 7-day moving average: {}", symbol);
        Map<LocalDate, BigDecimal> movingAvg = priceAnalysisService.getMovingAverage7Days(symbol);
        return ResponseEntity.ok(movingAvg);
    }
    
    @GetMapping("/price/{symbol}/moving-average/30")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getMovingAverage30Days(@PathVariable String symbol) {
        logger.info("GET request for 30-day moving average: {}", symbol);
        Map<LocalDate, BigDecimal> movingAvg = priceAnalysisService.getMovingAverage30Days(symbol);
        return ResponseEntity.ok(movingAvg);
    }
    
    @GetMapping("/price/{symbol}/average-volatility")
    public ResponseEntity<BigDecimal> getAverageVolatility(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for average volatility: {} from {} to {}", symbol, startDate, endDate);
        BigDecimal avgVolatility = priceAnalysisService.getAverageVolatility(symbol, startDate, endDate);
        return ResponseEntity.ok(avgVolatility);
    }
    
    @GetMapping("/volume/{symbol}/vwap")
    public ResponseEntity<BigDecimal> getVWAP(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for VWAP: {} from {} to {}", symbol, startDate, endDate);
        BigDecimal vwap = volumeAnalysisService.calculateVWAP(symbol, startDate, endDate);
        return ResponseEntity.ok(vwap);
    }
    
    @GetMapping("/volume/{symbol}/turnover")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getDailyTurnover(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for daily turnover: {} from {} to {}", symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> turnover = volumeAnalysisService.getDailyTurnoverForPeriod(symbol, startDate, endDate);
        return ResponseEntity.ok(turnover);
    }
    
    @GetMapping("/volume/{symbol}/liquidity")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getLiquidityRatio(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for liquidity ratio: {} from {} to {}", symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> liquidity = volumeAnalysisService.getLiquidityRatioForPeriod(symbol, startDate, endDate);
        return ResponseEntity.ok(liquidity);
    }
    
    @GetMapping("/volume/{symbol}/statistics")
    public ResponseEntity<Map<String, Object>> getVolumeStatistics(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for volume statistics: {} from {} to {}", symbol, startDate, endDate);
        Map<String, Object> stats = volumeAnalysisService.getVolumeStatistics(symbol, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/volume/top-stocks")
    public ResponseEntity<List<Map<String, Object>>> getTopVolumeStocks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("GET request for top {} volume stocks from {} to {}", limit, startDate, endDate);
        List<Map<String, Object>> topStocks = volumeAnalysisService.getTopVolumeStocks(startDate, endDate, limit);
        return ResponseEntity.ok(topStocks);
    }
    
    @GetMapping("/summary/{symbol}")
    public ResponseEntity<Map<String, Object>> getStockSummary(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET request for stock summary: {} from {} to {}", symbol, startDate, endDate);
        
        Map<String, Object> summary = new HashMap<>();
        
        // Price Analytics
        summary.put("averageVolatility", priceAnalysisService.getAverageVolatility(symbol, startDate, endDate));
        
        // Volume Analytics
        summary.put("vwap", volumeAnalysisService.calculateVWAP(symbol, startDate, endDate));
        summary.put("averageVolume", volumeAnalysisService.calculateAverageVolume(symbol, startDate, endDate));
        summary.put("volumeStatistics", volumeAnalysisService.getVolumeStatistics(symbol, startDate, endDate));
        
        return ResponseEntity.ok(summary);
    }
}