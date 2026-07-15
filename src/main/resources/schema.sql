CREATE DATABASE IF NOT EXISTS accountdb;
USE accountdb;

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile_number VARCHAR(20) NOT NULL UNIQUE,
    account_type ENUM('SAVING', 'CURRENT', 'SALARY') NOT NULL,
    cibil_score INT NOT NULL CHECK (cibil_score BETWEEN 300 AND 900),
    branch_address VARCHAR(255) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

);


CREATE TABLE IF NOT EXISTS account_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(255) NOT NULL,
    account_holder_name VARCHAR(255) NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    amount DECIMAL(38, 2) NOT NULL,
    balance_after_transaction DECIMAL(38, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    transaction_date DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);
