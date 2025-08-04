package com.revstox.stock_analyzer.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockAnalytics {
    private Long id;
    private String symbol;
    private LocalDate tradeDate;
    private BigDecimal volatility;
    private BigDecimal priceChange;
    private BigDecimal movingAvg7;
    private BigDecimal movingAvg30;
    private BigDecimal movingAvg90;
    private BigDecimal vwapCalculated;
    private BigDecimal dailyTurnover;
    private BigDecimal priceGap;
    
    // Constructors
    public StockAnalytics() {}
    
    public StockAnalytics(String symbol, LocalDate tradeDate) {
        this.symbol = symbol;
        this.tradeDate = tradeDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
    
    public BigDecimal getVolatility() { return volatility; }
    public void setVolatility(BigDecimal volatility) { this.volatility = volatility; }
    
    public BigDecimal getPriceChange() { return priceChange; }
    public void setPriceChange(BigDecimal priceChange) { this.priceChange = priceChange; }
    
    public BigDecimal getMovingAvg7() { return movingAvg7; }
    public void setMovingAvg7(BigDecimal movingAvg7) { this.movingAvg7 = movingAvg7; }
    
    public BigDecimal getMovingAvg30() { return movingAvg30; }
    public void setMovingAvg30(BigDecimal movingAvg30) { this.movingAvg30 = movingAvg30; }
    
    public BigDecimal getMovingAvg90() { return movingAvg90; }
    public void setMovingAvg90(BigDecimal movingAvg90) { this.movingAvg90 = movingAvg90; }
    
    public BigDecimal getVwapCalculated() { return vwapCalculated; }
    public void setVwapCalculated(BigDecimal vwapCalculated) { this.vwapCalculated = vwapCalculated; }
    
    public BigDecimal getDailyTurnover() { return dailyTurnover; }
    public void setDailyTurnover(BigDecimal dailyTurnover) { this.dailyTurnover = dailyTurnover; }
    
    public BigDecimal getPriceGap() { return priceGap; }
    public void setPriceGap(BigDecimal priceGap) { this.priceGap = priceGap; }
}