#!/bin/bash
set -e
export LANG=C.UTF-8
export LC_ALL=C.UTF-8

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS new_payment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE USER IF NOT EXISTS 'payment_service'@'%' IDENTIFIED BY '${PAYMENT_SERVICE_DB_PASSWORD}';
    GRANT ALL PRIVILEGES ON new_payment.* TO 'payment_service'@'%';

    FLUSH PRIVILEGES;

    USE new_payment;

    CREATE TABLE IF NOT EXISTS payments (
        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
        payment_key   VARCHAR(255) UNIQUE NOT NULL,
        order_id      VARCHAR(36) NOT NULL,
        amount        INT NOT NULL,
        status        VARCHAR(20) NOT NULL,
        created_at    DATETIME NOT NULL,
        updated_at    DATETIME NOT NULL,
        INDEX idx_order_id (order_id)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOSQL