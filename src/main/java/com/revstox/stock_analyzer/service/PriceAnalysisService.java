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
public class PriceAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(PriceAnalysisService.class);
    
    @Autowired
    private DailyPriceDao dailyPriceDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public BigDecimal calculateVolatility(DailyPrice price) {
        if (price.getOpen().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal range = price.getHigh().subtract(price.getLow());
        return range.divide(price.getOpen(), 4, RoundingMode.HALF_UP)
                   .multiply(new BigDecimal("100"));
    }
    
    public BigDecimal calculatePriceChange(DailyPrice price) {
        if (price.getOpen().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal change = price.getClose().subtract(price.getOpen());
        return change.divide(price.getOpen(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
    }
    
    public Map<LocalDate, BigDecimal> getVolatilityForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating volatility for {} from {} to {}", symbol, startDate, endDate);
        
        List<DailyPrice> prices = dailyPriceDao.findBySymbolAndDateRange(symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> volatilityMap = new HashMap<>();
        
        for (DailyPrice price : prices) {
            BigDecimal volatility = calculateVolatility(price);
            volatilityMap.put(price.getDate(), volatility);
        }
        
        return volatilityMap;
    }
    
    public Map<LocalDate, BigDecimal> getPriceChangesForPeriod(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating price changes for {} from {} to {}", symbol, startDate, endDate);
        
        List<DailyPrice> prices = dailyPriceDao.findBySymbolAndDateRange(symbol, startDate, endDate);
        Map<LocalDate, BigDecimal> priceChangeMap = new HashMap<>();
        
        for (DailyPrice price : prices) {
            BigDecimal priceChange = calculatePriceChange(price);
            priceChangeMap.put(price.getDate(), priceChange);
        }
        
        return priceChangeMap;
    }
    
    public Map<LocalDate, BigDecimal> getMovingAverage7Days(String symbol) {
        logger.debug("Calculating 7-day moving average for {}", symbol);
        
        String query = "SELECT trade_date, AVG(close_price) OVER (PARTITION BY symbol ORDER BY trade_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) as moving_avg_7 FROM daily_prices WHERE symbol = ? ORDER BY trade_date";
        
        Map<LocalDate, BigDecimal> movingAvgMap = new HashMap<>();
        
        jdbcTemplate.query(query, (rs) -> {
            LocalDate date = rs.getDate("trade_date").toLocalDate();
            BigDecimal avgPrice = rs.getBigDecimal("moving_avg_7");
            movingAvgMap.put(date, avgPrice);
        }, symbol);
        
        return movingAvgMap;
    }
    
    public Map<LocalDate, BigDecimal> getMovingAverage30Days(String symbol) {
        logger.debug("Calculating 30-day moving average for {}", symbol);
        
        String query = "SELECT trade_date, AVG(close_price) OVER (PARTITION BY symbol ORDER BY trade_date ROWS BETWEEN 29 PRECEDING AND CURRENT ROW) as moving_avg_30 FROM daily_prices WHERE symbol = ? ORDER BY trade_date";
        
        Map<LocalDate, BigDecimal> movingAvgMap = new HashMap<>();
        
        jdbcTemplate.query(query, (rs) -> {
            LocalDate date = rs.getDate("trade_date").toLocalDate();
            BigDecimal avgPrice = rs.getBigDecimal("moving_avg_30");
            movingAvgMap.put(date, avgPrice);
        }, symbol);
        
        return movingAvgMap;
    }
    
    public BigDecimal getAverageVolatility(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating average volatility for {} from {} to {}", symbol, startDate, endDate);
        
        String query = "SELECT AVG((high_price - low_price) / open_price * 100) as avg_volatility FROM daily_prices WHERE symbol = ? AND trade_date BETWEEN ? AND ? AND open_price > 0";
        
        BigDecimal avgVolatility = jdbcTemplate.queryForObject(query, BigDecimal.class, symbol, startDate, endDate);
        return avgVolatility != null ? avgVolatility : BigDecimal.ZERO;
    }
}
