#!/bin/bash
set -e
export LANG=C.UTF-8
export LC_ALL=C.UTF-8

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

    CREATE USER IF NOT EXISTS 'user_service'@'%' IDENTIFIED BY '${USER_SERVICE_DB_PASSWORD}';
    GRANT ALL PRIVILEGES ON user_db.* TO 'user_service'@'%';

    FLUSH PRIVILEGES;

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
EOSQL