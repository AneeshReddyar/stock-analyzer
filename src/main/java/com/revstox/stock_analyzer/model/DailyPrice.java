package com.revstox.stock_analyzer.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyPrice {
    private Long id;
    private LocalDate date;
    private String symbol;
    private String series;
    private BigDecimal prevClose;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal last;
    private BigDecimal close;
    private BigDecimal vwap;
    private Long volume;
    private BigDecimal turnover;
    private Integer trades;
    private Long deliverableVolume;
    private BigDecimal deliverablePercent;
    
    // Constructors
    public DailyPrice() {}
    
    public DailyPrice(LocalDate date, String symbol, String series, 
                     BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close) {
        this.date = date;
        this.symbol = symbol;
        this.series = series;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    
    public BigDecimal getPrevClose() { return prevClose; }
    public void setPrevClose(BigDecimal prevClose) { this.prevClose = prevClose; }
    
    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }
    
    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }
    
    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }
    
    public BigDecimal getLast() { return last; }
    public void setLast(BigDecimal last) { this.last = last; }
    
    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }
    
    public BigDecimal getVwap() { return vwap; }
    public void setVwap(BigDecimal vwap) { this.vwap = vwap; }
    
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    
    public BigDecimal getTurnover() { return turnover; }
    public void setTurnover(BigDecimal turnover) { this.turnover = turnover; }
    
    public Integer getTrades() { return trades; }
    public void setTrades(Integer trades) { this.trades = trades; }
    
    public Long getDeliverableVolume() { return deliverableVolume; }
    public void setDeliverableVolume(Long deliverableVolume) { this.deliverableVolume = deliverableVolume; }
    
    public BigDecimal getDeliverablePercent() { return deliverablePercent; }
    public void setDeliverablePercent(BigDecimal deliverablePercent) { this.deliverablePercent = deliverablePercent; }
}
