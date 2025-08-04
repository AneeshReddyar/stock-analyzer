package com.revstox.stock_analyzer.dao;

import java.util.List;

import com.revstox.stock_analyzer.model.Stock;

public interface StockDao {
    void save(Stock stock);
    Stock findBySymbol(String symbol);
    List<Stock> findAll();
    void update(Stock stock);
    void deleteBySymbol(String symbol);
    boolean exists(String symbol);
}