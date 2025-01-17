-- 기존 테이블 삭제
DROP TABLE IF EXISTS point_history;
DROP TABLE IF EXISTS point;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS coupon_issuance;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS users;

-- Users 테이블 생성
CREATE TABLE users
(
    id         VARCHAR(50) PRIMARY KEY NOT NULL,
    name       VARCHAR(100)            NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Category 테이블 생성
CREATE TABLE category
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Product 테이블 생성
CREATE TABLE product
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    price       BIGINT       NOT NULL,
    stock       INT          NOT NULL,
    category_id BIGINT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Coupon 테이블 생성
CREATE TABLE coupon
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                VARCHAR(100) NOT NULL,
    discount_amount      BIGINT       NOT NULL,
    minimum_order_amount BIGINT       NOT NULL,
    remaining_quantity   INT          NOT NULL,
    total_quantity       INT          NOT NULL,
    expiration_date      DATE         NOT NULL
);

-- Coupon_Issuance 테이블 생성
CREATE TABLE coupon_issuance
(
    id        VARCHAR(36) PRIMARY KEY NOT NULL,
    user_id   VARCHAR(50)             NOT NULL,
    coupon_id BIGINT                  NOT NULL,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at   TIMESTAMP,
    status    VARCHAR(20)             NOT NULL,
    UNIQUE (user_id, coupon_id)
);

-- Orders 테이블 생성
CREATE TABLE orders
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            VARCHAR(50) NOT NULL,
    total_amount       BIGINT      NOT NULL DEFAULT 0,
    discount_amount    BIGINT,
    final_amount       BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    coupon_issuance_id VARCHAR(36),
    status             VARCHAR(20) NOT NULL
);

-- Order_Detail 테이블 생성
CREATE TABLE order_detail
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT       NOT NULL,
    product_id   BIGINT       NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity     INT          NOT NULL,
    unit_price   BIGINT       NOT NULL,
    sub_total    BIGINT       NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment 테이블 생성
CREATE TABLE payment
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT      NOT NULL,
    amount     BIGINT      NOT NULL,
    paid_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status     VARCHAR(20) NOT NULL
);

-- Point 테이블 생성
CREATE TABLE point
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    VARCHAR(50) NOT NULL,
    balance    BIGINT      NOT NULL DEFAULT 0,
    updated_at TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Point_History 테이블 생성
CREATE TABLE point_history
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    point_id   BIGINT      NOT NULL,
    type       VARCHAR(20) NOT NULL,
    amount     BIGINT      NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
