package com.revstox.stock_analyzer.dao.impl;

import com.revstox.stock_analyzer.dao.StockDao;
import com.revstox.stock_analyzer.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@PropertySource("classpath:queries.properties")
public class StockDaoImpl implements StockDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${query.stock.insert}")
    private String insertQuery;
    
    @Value("${query.stock.findBySymbol}")
    private String findBySymbolQuery;
    
    @Value("${query.stock.findAll}")
    private String findAllQuery;
    
    private final RowMapper<Stock> rowMapper = new StockRowMapper();
    
    @Override
    public void save(Stock stock) {
        jdbcTemplate.update(insertQuery, 
            stock.getSymbol(), 
            stock.getCompanyName(), 
            stock.getSeries());
    }
    
    @Override
    public Stock findBySymbol(String symbol) {
        List<Stock> results = jdbcTemplate.query(findBySymbolQuery, rowMapper, symbol);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<Stock> findAll() {
        return jdbcTemplate.query(findAllQuery, rowMapper);
    }
    
    @Override
    public void update(Stock stock) {
        jdbcTemplate.update(
            "UPDATE stocks SET company_name = ?, series = ?, sector = ? WHERE symbol = ?",
            stock.getCompanyName(), stock.getSeries(), stock.getSector(), stock.getSymbol());
    }
    
    @Override
    public void deleteBySymbol(String symbol) {
        jdbcTemplate.update("DELETE FROM stocks WHERE symbol = ?", symbol);
    }
    
    @Override
    public boolean exists(String symbol) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM stocks WHERE symbol = ?", Integer.class, symbol);
        return count != null && count > 0;
    }
    
    private static class StockRowMapper implements RowMapper<Stock> {
        @Override
        public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stock stock = new Stock();
            stock.setSymbol(rs.getString("symbol"));
            stock.setCompanyName(rs.getString("company_name"));
            stock.setSeries(rs.getString("series"));
            stock.setSector(rs.getString("sector"));
            return stock;
        }
    }
}