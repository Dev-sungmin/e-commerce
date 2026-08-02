#!/bin/bash
set -e
export LANG=C.UTF-8
export LC_ALL=C.UTF-8

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    -- User Service
    CREATE DATABASE IF NOT EXISTS user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE USER IF NOT EXISTS 'user_service'@'%' IDENTIFIED BY '${USER_SERVICE_DB_PASSWORD}';
    GRANT ALL PRIVILEGES ON user_db.* TO 'user_service'@'%';

    USE user_db;

    CREATE TABLE IF NOT EXISTS users (
        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
        email         VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        role          VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
        created_at    DATETIME NOT NULL
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE TABLE IF NOT EXISTS refresh_tokens (
        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id       BIGINT NOT NULL,
        token         VARCHAR(512) UNIQUE NOT NULL,
        expires_at    DATETIME NOT NULL,
        created_at    DATETIME NOT NULL,
        INDEX idx_user_id (user_id),
        INDEX idx_token (token)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    -- PHP 레거시 (상품/재고)
    CREATE DATABASE IF NOT EXISTS legacy_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE USER IF NOT EXISTS 'legacy_shop_service'@'%' IDENTIFIED BY '${LEGACY_SHOP_DB_PASSWORD}';
    GRANT ALL PRIVILEGES ON legacy_shop.* TO 'legacy_shop_service'@'%';

    FLUSH PRIVILEGES;

    USE legacy_shop;

    CREATE TABLE IF NOT EXISTS inventory (
        id             BIGINT AUTO_INCREMENT PRIMARY KEY,
        name           VARCHAR(255) NOT NULL,
        price          INT NOT NULL,
        stock_quantity INT NOT NULL DEFAULT 0,
        image_url      VARCHAR(500) DEFAULT NULL
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE TABLE IF NOT EXISTS inventory_log (
        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
        product_id    BIGINT NOT NULL,
        order_id      VARCHAR(36) NOT NULL,
        change_amount INT NOT NULL,
        reason        VARCHAR(20) NOT NULL,
        created_at    DATETIME NOT NULL,
        INDEX idx_order_id (order_id),
        INDEX idx_product_id (product_id)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    INSERT INTO inventory (name, price, stock_quantity, image_url) VALUES
        ('아메리카노', 3500, 50, 'https://e-commerce-product-images-sungmin2026.s3.ap-northeast-2.amazonaws.com/americano.jpg'),
        ('카페라떼', 4000, 30, 'https://e-commerce-product-images-sungmin2026.s3.ap-northeast-2.amazonaws.com/cafelatte.jpg'),
        ('에스프레소', 3000, 20, 'https://e-commerce-product-images-sungmin2026.s3.ap-northeast-2.amazonaws.com/espresso.jpg');
EOSQL