package com.revstox.stock_analyzer.dao.impl;

import com.revstox.stock_analyzer.dao.DailyPriceDao;
import com.revstox.stock_analyzer.model.DailyPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@PropertySource("classpath:queries.properties")
public class DailyPriceDaoImpl implements DailyPriceDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${query.dailyprice.insert}")
    private String insertQuery;
    
    @Value("${query.dailyprice.findBySymbol}")
    private String findBySymbolQuery;
    
    @Value("${query.dailyprice.findBySymbolAndDateRange}")
    private String findBySymbolAndDateRangeQuery;
    
    private final RowMapper<DailyPrice> rowMapper = new DailyPriceRowMapper();
    
    @Override
    public void save(DailyPrice dailyPrice) {
        jdbcTemplate.update(insertQuery, 
            dailyPrice.getDate(),
            dailyPrice.getSymbol(),
            dailyPrice.getSeries(),
            dailyPrice.getPrevClose(),
            dailyPrice.getOpen(),
            dailyPrice.getHigh(),
            dailyPrice.getLow(),
            dailyPrice.getLast(),
            dailyPrice.getClose(),
            dailyPrice.getVwap(),
            dailyPrice.getVolume(),
            dailyPrice.getTurnover(),
            dailyPrice.getTrades(),
            dailyPrice.getDeliverableVolume(),
            dailyPrice.getDeliverablePercent()
        );
    }
    
    @Override
    public void saveAll(List<DailyPrice> dailyPrices) {
        for (DailyPrice price : dailyPrices) {
            save(price);
        }
    }
    
    @Override
    public List<DailyPrice> findBySymbol(String symbol) {
        return jdbcTemplate.query(findBySymbolQuery, rowMapper, symbol);
    }
    
    @Override
    public List<DailyPrice> findBySymbolAndDateRange(String symbol, LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.query(findBySymbolAndDateRangeQuery, rowMapper, symbol, startDate, endDate);
    }
    
    @Override
    public DailyPrice findBySymbolAndDate(String symbol, LocalDate date) {
        List<DailyPrice> results = jdbcTemplate.query(
            "SELECT * FROM daily_prices WHERE symbol = ? AND trade_date = ?", 
            rowMapper, symbol, date);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<DailyPrice> findAll() {
        return jdbcTemplate.query("SELECT * FROM daily_prices ORDER BY trade_date DESC", rowMapper);
    }
    
    @Override
    public void deleteBySymbol(String symbol) {
        jdbcTemplate.update("DELETE FROM daily_prices WHERE symbol = ?", symbol);
    }
    
    private static class DailyPriceRowMapper implements RowMapper<DailyPrice> {
        @Override
        public DailyPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
            DailyPrice price = new DailyPrice();
            price.setId(rs.getLong("id"));
            price.setDate(rs.getDate("trade_date").toLocalDate());
            price.setSymbol(rs.getString("symbol"));
            price.setSeries(rs.getString("series"));
            price.setPrevClose(rs.getBigDecimal("prev_close"));
            price.setOpen(rs.getBigDecimal("open_price"));
            price.setHigh(rs.getBigDecimal("high_price"));
            price.setLow(rs.getBigDecimal("low_price"));
            price.setLast(rs.getBigDecimal("last_price"));
            price.setClose(rs.getBigDecimal("close_price"));
            price.setVwap(rs.getBigDecimal("vwap"));
            price.setVolume(rs.getLong("volume"));
            price.setTurnover(rs.getBigDecimal("turnover"));
            price.setTrades(rs.getInt("trades"));
            price.setDeliverableVolume(rs.getLong("deliverable_volume"));
            price.setDeliverablePercent(rs.getBigDecimal("deliverable_percent"));
            return price;
        }
    }
}