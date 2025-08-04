package com.revstox.stock_analyzer.model;

public class Stock {
    private String symbol;
    private String companyName;
    private String sector;
    private String series;
    
    public Stock() {}
    
    public Stock(String symbol, String companyName, String series) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.series = series;
    }
    
    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
}