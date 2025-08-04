package com.revstox.stock_analyzer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.revstox.stock_analyzer.model.DailyPrice;
import com.revstox.stock_analyzer.model.Stock;

@Component
public class CsvReader {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvReader.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public List<DailyPrice> readDailyPrices(InputStream inputStream) {
        List<DailyPrice> dailyPrices = new ArrayList<>();
        Set<String> symbols = new HashSet<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (isFirstLine) {
                    isFirstLine = false;
                    logger.info("Skipping header: {}", line);
                    continue;
                }
                
                try {
                    DailyPrice dailyPrice = parseDailyPriceLine(line, lineNumber);
                    if (dailyPrice != null) {
                        dailyPrices.add(dailyPrice);
                        symbols.add(dailyPrice.getSymbol());
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing line {}: {} - {}", lineNumber, line, e.getMessage());
                }
            }
            
            logger.info("Successfully parsed {} daily price records for {} unique symbols", 
                       dailyPrices.size(), symbols.size());
            
        } catch (IOException e) {
            logger.error("Error reading CSV file", e);
        }
        
        return dailyPrices;
    }
    
    private DailyPrice parseDailyPriceLine(String line, int lineNumber) {
        // CSV columns: Date,Symbol,Series,Prev Close,Open,High,Low,Last,Close,VWAP,Volume,Turnover,Trades,Deliverable Volume,%Deliverble
        String[] parts = line.split(",");
        
        if (parts.length < 15) {
            logger.warn("Line {}: Expected 15 columns, found {}", lineNumber, parts.length);
            return null;
        }
        
        try {
            DailyPrice price = new DailyPrice();
            
            // Parse date - handle different formats
            String dateStr = parts[0].trim();
            price.setDate(parseDate(dateStr));
            
            price.setSymbol(parts[1].trim());
            price.setSeries(parts[2].trim());
            price.setPrevClose(parseBigDecimal(parts[3]));
            price.setOpen(parseBigDecimal(parts[4]));
            price.setHigh(parseBigDecimal(parts[5]));
            price.setLow(parseBigDecimal(parts[6]));
            price.setLast(parseBigDecimal(parts[7]));
            price.setClose(parseBigDecimal(parts[8]));
            price.setVwap(parseBigDecimal(parts[9]));
            price.setVolume(parseLong(parts[10]));
            price.setTurnover(parseBigDecimal(parts[11]));
            price.setTrades(parseInteger(parts[12]));
            price.setDeliverableVolume(parseLong(parts[13]));
            price.setDeliverablePercent(parseBigDecimal(parts[14]));
            
            return price;
            
        } catch (Exception e) {
            logger.warn("Error parsing line {}: {}", lineNumber, e.getMessage());
            return null;
        }
    }
    
    private LocalDate parseDate(String dateStr) {
        try {
            // Try common date formats
            if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } else if (dateStr.contains("/")) {
                // Handle MM/dd/yyyy or dd/MM/yyyy format
                DateTimeFormatter slashFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                return LocalDate.parse(dateStr, slashFormatter);
            }
        } catch (DateTimeParseException e) {
            logger.warn("Could not parse date: {}", dateStr);
        }
        return null;
    }
    
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || "0".equals(value.trim())) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public List<Stock> extractUniqueStocks(List<DailyPrice> dailyPrices) {
        Set<String> uniqueSymbols = new HashSet<>();
        List<Stock> stocks = new ArrayList<>();
        
        for (DailyPrice price : dailyPrices) {
            if (!uniqueSymbols.contains(price.getSymbol())) {
                uniqueSymbols.add(price.getSymbol());
                Stock stock = new Stock();
                stock.setSymbol(price.getSymbol());
                stock.setSeries(price.getSeries());
                stock.setCompanyName(price.getSymbol()); // Will be updated later
                stocks.add(stock);
            }
        }
        
        logger.info("Extracted {} unique stocks", stocks.size());
        return stocks;
    }
}