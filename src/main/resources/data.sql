-- Users 데이터 삽입 (이미 존재하면 무시)
INSERT INTO users (id, name)
VALUES ('user1', '홍길동'),
       ('user2', '김철수'),
       ('user3', '이영희')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Category 데이터 삽입
INSERT INTO category (name)
VALUES ('전자제품'),
       ('생활용품'),
       ('도서')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Product 데이터 삽입
INSERT INTO product (name, price, stock, category_id)
VALUES ('노트북', 1500000, 10, 1),
       ('스마트폰', 1000000, 15, 1),
       ('청소기', 200000, 30, 2),
       ('책상', 120000, 20, 2),
       ('자바 프로그래밍', 35000, 50, 3)
ON DUPLICATE KEY UPDATE name  = VALUES(name),
                        price = VALUES(price),
                        stock = VALUES(stock);

-- Coupon 데이터 삽입
INSERT INTO coupon (title, discount_amount, minimum_order_amount, remaining_quantity, total_quantity, expiration_date)
VALUES ('신규회원 할인', 5000, 30000, 100, 100, '2025-12-31'),
       ('10000원 할인 쿠폰', 10000, 50000, 50, 50, '2025-12-31')
ON DUPLICATE KEY UPDATE title           = VALUES(title),
                        discount_amount = VALUES(discount_amount);

-- Coupon_Issuance 데이터 삽입
INSERT INTO coupon_issuance (id, user_id, coupon_id, status)
VALUES (UUID(), 'user1', 1, 'ISSUED'),
       (UUID(), 'user2', 2, 'USED')
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Orders 데이터 삽입
INSERT INTO orders (user_id, total_amount, discount_amount, final_amount, coupon_issuance_id, status)
VALUES ('user1', 150000, 5000, 145000, NULL, 'COMPLETED'),
       ('user2', 200000, 10000, 190000, NULL, 'PENDING_PAYMENT')
ON DUPLICATE KEY UPDATE total_amount = VALUES(total_amount),
                        final_amount = VALUES(final_amount);

-- Order_Detail 데이터 삽입
INSERT INTO order_detail (order_id, product_id, product_name, quantity, unit_price, sub_total)
VALUES (1, 1, '노트북', 1, 1500000, 1500000),
       (1, 2, '스마트폰', 2, 1000000, 2000000),
       (1, 3, '청소기', 3, 200000, 600000),
       (2, 2, '스마트폰', 1, 1000000, 1000000),
       (2, 3, '청소기', 2, 200000, 400000),
       (2, 4, '책상', 3, 120000, 360000)
ON DUPLICATE KEY UPDATE product_name = VALUES(product_name),
                        quantity     = VALUES(quantity);

-- Payment 데이터 삽입
INSERT INTO payment (order_id, amount, status)
VALUES (1, 145000, 'COMPLETED'),
       (2, 190000, 'PENDING')
ON DUPLICATE KEY UPDATE amount = VALUES(amount),
                        status = VALUES(status);

-- Point 데이터 삽입
INSERT INTO point (user_id, balance)
VALUES ('user1', 10000),
       ('user2', 5000),
       ('user3', 0)
ON DUPLICATE KEY UPDATE balance = VALUES(balance);

-- Point_History 데이터 삽입
INSERT INTO point_history (point_id, type, amount)
VALUES (1, 'RECHARGE', 10000),
       (2, 'DEDUCT', 5000)
ON DUPLICATE KEY UPDATE amount = VALUES(amount);
