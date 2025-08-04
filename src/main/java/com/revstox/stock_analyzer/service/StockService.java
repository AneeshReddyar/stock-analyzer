package com.revstox.stock_analyzer.service;

import com.revstox.stock_analyzer.dao.DailyPriceDao;
import com.revstox.stock_analyzer.dao.StockDao;
import com.revstox.stock_analyzer.model.DailyPrice;
import com.revstox.stock_analyzer.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {
    
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);
    
    @Autowired
    private StockDao stockDao;
    
    @Autowired
    private DailyPriceDao dailyPriceDao;
    
    public List<Stock> getAllStocks() {
        logger.debug("Fetching all stocks");
        return stockDao.findAll();
    }
    
    public Stock getStockBySymbol(String symbol) {
        logger.debug("Fetching stock by symbol: {}", symbol);
        return stockDao.findBySymbol(symbol.toUpperCase());
    }
    
    public List<DailyPrice> getStockPrices(String symbol) {
        logger.debug("Fetching prices for symbol: {}", symbol);
        return dailyPriceDao.findBySymbol(symbol.toUpperCase());
    }
    
    public List<DailyPrice> getStockPricesInRange(String symbol, LocalDate startDate, LocalDate endDate) {
        logger.debug("Fetching prices for symbol: {} between {} and {}", symbol, startDate, endDate);
        return dailyPriceDao.findBySymbolAndDateRange(symbol.toUpperCase(), startDate, endDate);
    }
    
    public DailyPrice getStockPriceOnDate(String symbol, LocalDate date) {
        logger.debug("Fetching price for symbol: {} on date: {}", symbol, date);
        return dailyPriceDao.findBySymbolAndDate(symbol.toUpperCase(), date);
    }
    
    public void saveStock(Stock stock) {
        logger.debug("Saving stock: {}", stock.getSymbol());
        stock.setSymbol(stock.getSymbol().toUpperCase());
        stockDao.save(stock);
    }
    
    public void saveDailyPrice(DailyPrice dailyPrice) {
        logger.debug("Saving daily price for: {} on {}", dailyPrice.getSymbol(), dailyPrice.getDate());
        dailyPrice.setSymbol(dailyPrice.getSymbol().toUpperCase());
        dailyPriceDao.save(dailyPrice);
    }
    
    public boolean stockExists(String symbol) {
        return stockDao.exists(symbol.toUpperCase());
    }
    
    public void deleteStock(String symbol) {
        logger.debug("Deleting stock: {}", symbol);
        stockDao.deleteBySymbol(symbol.toUpperCase());
    }
}