#!/bin/bash
set -e
export LANG=C.UTF-8
export LC_ALL=C.UTF-8

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS new_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE USER IF NOT EXISTS 'order_service'@'%' IDENTIFIED BY '${ORDER_SERVICE_DB_PASSWORD}';
    GRANT ALL PRIVILEGES ON new_order.* TO 'order_service'@'%';

    FLUSH PRIVILEGES;

    USE new_order;

    CREATE TABLE IF NOT EXISTS orders (
        id            VARCHAR(36) PRIMARY KEY,
        user_id       BIGINT NOT NULL,
        total_amount  INT NOT NULL,
        status        VARCHAR(20) NOT NULL,
        created_at    DATETIME NOT NULL,
        updated_at    DATETIME NOT NULL,
        INDEX idx_user_created (user_id, created_at),
        INDEX idx_status_created (status, created_at)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE TABLE IF NOT EXISTS order_items (
        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
        order_id      VARCHAR(36) NOT NULL,
        product_id    BIGINT NOT NULL,
        product_name  VARCHAR(255) NOT NULL,
        quantity      INT NOT NULL,
        price         INT NOT NULL,
        FOREIGN KEY (order_id) REFERENCES orders(id),
        INDEX idx_order_id (order_id)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOSQL