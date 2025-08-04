package com.revstox.stock_analyzer.service;

import com.revstox.stock_analyzer.dao.DailyPriceDao;
import com.revstox.stock_analyzer.model.DailyPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VolumeAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(VolumeAnalysisService.class);
    
    @Autowired
    private DailyPriceDao dailyPriceDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public BigDecimal calculateVWAP(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating VWAP for {} from {} to {}", symbol, startDate, endDate);
        
        String query = """
            SELECT SUM(close_price * volume) / SUM(volume) as vwap 
            FROM daily_prices 
            WHERE symbol = ? AND trade_date BETWEEN ? AND ? AND volume > 0
            """;
        
        BigDecimal vwap = jdbcTemplate.queryForObject(query, BigDecimal.class, symbol, startDate, endDate);
        return vwap != null ? vwap.setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
    
    public BigDecimal calculateDailyTurnover(DailyPrice price) {
        if (price.getClose() == null || price.getVolume() == null) {
            return BigDecimal.ZERO;
        }
        return price.getClose().multiply(new BigDecimal(price.getVolume()));
    }
    
    public Map<LocalDate, BigDecimal> getDailyTurnoverForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating daily turnover for {} from {} to {}", symbol, startDate, endDate);
        
        List<DailyPrice> prices = dailyPriceDao.findBySymbolAndDateRange(symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> turnoverMap = new HashMap<>();
        
        for (DailyPrice price : prices) {
            BigDecimal turnover = calculateDailyTurnover(price);
            turnoverMap.put(price.getDate(), turnover);
        }
        
        return turnoverMap;
    }
    
    public Map<LocalDate, BigDecimal> getLiquidityRatioForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating liquidity ratio for {} from {} to {}", symbol, startDate, endDate);
        
        String query = """
            SELECT trade_date, 
                   CASE 
                       WHEN volume > 0 THEN (deliverable_volume * 100.0 / volume)
                       ELSE 0 
                   END as liquidity_ratio
            FROM daily_prices 
            WHERE symbol = ? AND trade_date BETWEEN ? AND ?
            ORDER BY trade_date
            """;
        
        Map<LocalDate, BigDecimal> liquidityMap = new HashMap<>();
        
        jdbcTemplate.query(query, (rs) -> {
            LocalDate date = rs.getDate("trade_date").toLocalDate();
            BigDecimal ratio = rs.getBigDecimal("liquidity_ratio");
            liquidityMap.put(date, ratio != null ? ratio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        }, symbol, startDate, endDate);
        
        return liquidityMap;
    }
    
    public Map<String, Object> getVolumeStatistics(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating volume statistics for {} from {} to {}", symbol, startDate, endDate);
        
        String query = """
            SELECT 
                AVG(volume) as avg_volume,
                MAX(volume) as max_volume,
                MIN(volume) as min_volume,
                SUM(volume) as total_volume,
                AVG(turnover) as avg_turnover,
                MAX(turnover) as max_turnover,
                MIN(turnover) as min_turnover,
                SUM(turnover) as total_turnover,
                AVG(deliverable_percent) as avg_deliverable_percent
            FROM daily_prices 
            WHERE symbol = ? AND trade_date BETWEEN ? AND ?
            """;
        
        Map<String, Object> stats = new HashMap<>();
        
        jdbcTemplate.query(query, (rs) -> {
            stats.put("averageVolume", rs.getLong("avg_volume"));
            stats.put("maxVolume", rs.getLong("max_volume"));
            stats.put("minVolume", rs.getLong("min_volume"));
            stats.put("totalVolume", rs.getLong("total_volume"));
            stats.put("averageTurnover", rs.getBigDecimal("avg_turnover"));
            stats.put("maxTurnover", rs.getBigDecimal("max_turnover"));
            stats.put("minTurnover", rs.getBigDecimal("min_turnover"));
            stats.put("totalTurnover", rs.getBigDecimal("total_turnover"));
            stats.put("averageDeliverablePercent", rs.getBigDecimal("avg_deliverable_percent"));
        }, symbol, startDate, endDate);
        
        return stats;
    }
    
    public List<Map<String, Object>> getTopVolumeStocks(LocalDate startDate, LocalDate endDate, int limit) {
        logger.debug("Getting top {} volume stocks from {} to {}", limit, startDate, endDate);
        
        String query = """
            SELECT symbol, SUM(volume) as total_volume, AVG(volume) as avg_volume
            FROM daily_prices 
            WHERE trade_date BETWEEN ? AND ?
            GROUP BY symbol 
            ORDER BY total_volume DESC 
            LIMIT ?
            """;
        
        return jdbcTemplate.queryForList(query, startDate, endDate, limit);
    }
    
    public BigDecimal calculateAverageVolume(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating average volume for {} from {} to {}", symbol, startDate, endDate);
        
        String query = """
            SELECT AVG(volume) as avg_volume 
            FROM daily_prices 
            WHERE symbol = ? AND trade_date BETWEEN ? AND ?
            """;
        
        BigDecimal avgVolume = jdbcTemplate.queryForObject(query, BigDecimal.class, symbol, startDate, endDate);
        return avgVolume != null ? avgVolume : BigDecimal.ZERO;
    }
}