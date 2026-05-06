CREATE DATABASE kho_hang;
USE kho_hang;

CREATE TABLE products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    in_stock BOOLEAN NOT NULL
);