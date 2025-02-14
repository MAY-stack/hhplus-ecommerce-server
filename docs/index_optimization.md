# 인덱스 적용

## 1. 인덱스 적용 쿼리

`WHERE절` 과 `GROUP BY`, `ORDER BY절` 이 모두 사용되는 인기판매상품 조회 쿼리

```sql
SELECT od.productId AS productId, SUM(od.quantity) AS totalQuantity
FROM OrderDetail od
WHERE od.createdAt >= :startDate
GROUP BY od.productId
ORDER BY totalQuantity DESC
LIMIT 5;
```

## 2. 테스트

- DBMS: MySQL
- 데이터 양: 약 10,000건
- 인덱스 적용 전후의 실행 계획(EXPLAIN ANALYZE) 및 실행 시간 비교

## 3. 단일 인덱스 (`created_at`)

### 3.1 인덱스 적용 전

- 실행 시간: **130ms**
- 실행 계획:
    - 전체 테이블 스캔 (Full Table Scan)
    - `created_at`을 필터링하지만 인덱스 없이 수행
    - 임시 테이블을 사용하여 그룹핑 및 정렬 수행

### 3.2 인덱스 적용

- 실행 시간: **20ms** (약 6.5배 성능 향상)
- 실행 계획:
    - `created_at` 인덱스를 활용한 **Index Range Scan** 수행
    - 전체 테이블 스캔 제거

## 4. 복합 인덱스 (`created_at, product_id`)

### 4.1 인덱스 적용 전

- 실행 시간: **109ms**
- 실행 계획:
    - `created_at` 필터링을 수행하지만 여전히 전체 테이블 스캔 발생
    - 임시 테이블을 사용하여 정렬

### 4.2 인덱스 적용

- 실행 시간: **24ms** (약 4.5배 성능 향상)
- 실행 계획:
    - `created_at` + `product_id` 복합 인덱스를 활용하여 검색 최적화
    - `product_id`가 `GROUP BY`에 포함되어 있어 인덱스가 효율적으로 사용됨

## 5. 커버링 인덱스 (`created_at, product_id, quantity`)

### 5.1 인덱스 적용 전

- 실행 시간: **105ms**
- 실행 계획:
    - `created_at` 필터링을 수행하지만 여전히 전체 테이블 스캔 발생
    - 임시 테이블을 사용하여 그룹핑 및 정렬

### 5.2 인덱스 적용

- 실행 시간: **15ms** (약 7배 성능 향상)
- 실행 계획:
    - **Covering Index Scan** 수행
    - 테이블 접근 없이 인덱스만으로 필요한 데이터를 조회
    - 가장 효율적인 실행 계획을 보여줌

## 6. 인덱스 적용

1. **단일 인덱스(`created_at`)만으로도 성능이 개선되지만, 복합 인덱스가 더 효율적**
2. **복합 인덱스(`created_at, product_id`) 적용 시 GROUP BY 최적화로 추가적인 성능 향상**
3. **커버링 인덱스(`created_at, product_id, quantity`)가 가장 좋은 성능을 보이며, 테이블 접근 없이 조회 가능**
4. **조회 패턴에 따라 적절한 인덱스 선택이 필요**

### 📌 최적의 인덱스 선택

- **읽기 성능을 최적화**하려면 커버링 인덱스를 고려하는 것이 효과적
- **데이터 변경이 빈번한 경우**에는 인덱스 유지 비용이 증가하므로 주의가 필요

**커버링 인덱스(`created_at, product_id, quantity`)가 가장 우수한 성능**을 보여서 커버링 인덱스를 적용