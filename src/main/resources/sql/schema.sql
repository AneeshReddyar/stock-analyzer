-- ===== schema.sql =====
-- Create database
CREATE DATABASE IF NOT EXISTS revstox_db;
USE revstox_db;

-- Stocks table
CREATE TABLE IF NOT EXISTS stocks (
    symbol VARCHAR(20) PRIMARY KEY,
    company_name VARCHAR(255),
    series VARCHAR(10),
    sector VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Daily prices table - main data from CSV
CREATE TABLE IF NOT EXISTS daily_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trade_date DATE NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    series VARCHAR(10),
    prev_close DECIMAL(15,4),
    open_price DECIMAL(15,4),
    high_price DECIMAL(15,4),
    low_price DECIMAL(15,4),
    last_price DECIMAL(15,4),
    close_price DECIMAL(15,4),
    vwap DECIMAL(15,4),
    volume BIGINT,
    turnover DECIMAL(20,4),
    trades INT,
    deliverable_volume BIGINT,
    deliverable_percent DECIMAL(8,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_symbol_date (symbol, trade_date),
    INDEX idx_symbol (symbol),
    INDEX idx_date (trade_date),
    INDEX idx_symbol_date (symbol, trade_date),
    FOREIGN KEY (symbol) REFERENCES stocks(symbol) ON DELETE CASCADE
);

-- Stock analytics table for calculated metrics
CREATE TABLE IF NOT EXISTS stock_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    trade_date DATE NOT NULL,
    volatility DECIMAL(10,4),
    price_change DECIMAL(10,4),
    moving_avg_7 DECIMAL(15,4),
    moving_avg_30 DECIMAL(15,4),
    moving_avg_90 DECIMAL(15,4),
    vwap_calculated DECIMAL(15,4),
    daily_turnover DECIMAL(20,4),
    price_gap DECIMAL(15,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_analytics (symbol, trade_date),
    INDEX idx_analytics_symbol (symbol),
    INDEX idx_analytics_date (trade_date),
    FOREIGN KEY (symbol) REFERENCES stocks(symbol) ON DELETE CASCADE
);