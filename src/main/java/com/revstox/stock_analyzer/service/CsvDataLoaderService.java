package com.revstox.stock_analyzer.service;

import com.revstox.stock_analyzer.dao.DailyPriceDao;
import com.revstox.stock_analyzer.dao.StockDao;
import com.revstox.stock_analyzer.model.DailyPrice;
import com.revstox.stock_analyzer.model.Stock;
import com.revstox.stock_analyzer.util.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
public class CsvDataLoaderService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvDataLoaderService.class);
    
    @Autowired
    private CsvReader csvReader;
    
    @Autowired
    private DailyPriceDao dailyPriceDao;
    
    @Autowired
    private StockDao stockDao;
    
    @Value("${app.csv.file.path:data/stock_data.csv}")
    private String csvFilePath;
    
    @Value("${app.batch.size:1000}")
    private int batchSize;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting CSV data loading process...");
        
        try {
            ClassPathResource resource = new ClassPathResource(csvFilePath);
            if (!resource.exists()) {
                logger.warn("CSV file not found at: {}. Skipping data load.", csvFilePath);
                return;
            }
            
            InputStream inputStream = resource.getInputStream();
            List<DailyPrice> dailyPrices = csvReader.readDailyPrices(inputStream);
            
            if (dailyPrices.isEmpty()) {
                logger.warn("No data found in CSV file");
                return;
            }
            
            // First, save unique stocks
            List<Stock> stocks = csvReader.extractUniqueStocks(dailyPrices);
            logger.info("Saving {} unique stocks...", stocks.size());
            
            for (Stock stock : stocks) {
                try {
                    stockDao.save(stock);
                } catch (Exception e) {
                    logger.warn("Error saving stock {}: {}", stock.getSymbol(), e.getMessage());
                }
            }
            
            // Then save daily prices in batches
            logger.info("Saving {} daily price records in batches of {}...", dailyPrices.size(), batchSize);
            
            for (int i = 0; i < dailyPrices.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, dailyPrices.size());
                List<DailyPrice> batch = dailyPrices.subList(i, endIndex);
                
                try {
                    dailyPriceDao.saveAll(batch);
                    logger.info("Saved batch {}-{} of {}", i + 1, endIndex, dailyPrices.size());
                } catch (Exception e) {
                    logger.error("Error saving batch {}-{}: {}", i + 1, endIndex, e.getMessage());
                    // Try saving individually
                    for (DailyPrice price : batch) {
                        try {
                            dailyPriceDao.save(price);
                        } catch (Exception ex) {
                            logger.warn("Error saving individual record for {} on {}: {}", 
                                       price.getSymbol(), price.getDate(), ex.getMessage());
                        }
                    }
                }
            }
            
            logger.info("CSV data loading completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error during CSV data loading", e);
            throw e;
        }
    }
}