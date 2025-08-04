package com.revstox.stock_analyzer.dao;

import java.time.LocalDate;
import java.util.List;

import com.revstox.stock_analyzer.model.DailyPrice;

public interface DailyPriceDao {
    void save(DailyPrice dailyPrice);
    void saveAll(List<DailyPrice> dailyPrices);
    List<DailyPrice> findBySymbol(String symbol);
    List<DailyPrice> findBySymbolAndDateRange(String symbol, LocalDate startDate, LocalDate endDate);
    DailyPrice findBySymbolAndDate(String symbol, LocalDate date);
    List<DailyPrice> findAll();
    void deleteBySymbol(String symbol);
}