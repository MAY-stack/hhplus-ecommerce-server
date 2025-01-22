# 동시성과 관련된 문제

## **1️⃣ Dirty Read (더티 리드)**

💡 **"커밋되지 않은 데이터를 다른 트랜잭션이 읽는 문제"**

### **📌 개념**

- 하나의 트랜잭션(T1)이 데이터를 변경했지만, **아직 커밋하지 않은 상태에서** 다른 트랜잭션(T2)이 해당 데이터를 읽는 경우 발생
- 만약 T1이 **ROLLBACK(롤백)** 하면, T2가 읽은 데이터는 **존재하지 않는 잘못된 데이터**가 됨

### **📌 예제 (포인트 잔액 업데이트)**

1. **T1**: `UPDATE point SET balance = 500 WHERE id = 1;`
2. **T2**: `SELECT balance FROM point WHERE id = 1;` → `500` (커밋되지 않은 값 읽음)
3. **T1**: `ROLLBACK;` (원래 값 복구)
4. **T2**가 읽은 `500`은 실제 존재하지 않는 값

### **📌 해결 방법**

✅ **트랜잭션 격리 수준을 `READ COMMITTED` 이상으로 설정**

---

## **2️⃣ Non-Repeatable Read (비반복 가능 읽기)**

💡 **"같은 데이터를 두 번 읽을 때 값이 달라지는 문제"**

### **📌 개념**

- 한 트랜잭션(T1)이 같은 데이터를 두 번 조회했을 때, **다른 트랜잭션(T2)이 데이터를 수정 및 커밋하면 값이 달라지는 현상**
- 즉, **같은 조건으로 SELECT 했는데 결과가 다르게 나옴**

### **📌 예제**

1. **T1**: `SELECT balance FROM accounts WHERE id = 1;` → `100`
2. **T2**: `UPDATE accounts SET balance = 200 WHERE id = 1; COMMIT;`
3. **T1**: `SELECT balance FROM accounts WHERE id = 1;` → `200` (이전과 값이 달라짐)

### **📌 해결 방법**

✅ **트랜잭션 격리 수준을 `REPEATABLE READ` 이상으로 설정**

---

## **3️⃣ Phantom Read (팬텀 리드)**

💡 **"같은 조건으로 조회했는데 새로운 데이터가 나타나는 문제"**

### **📌 개념**

- 한 트랜잭션(T1)이 동일한 `SELECT` 쿼리를 실행했을 때, **다른 트랜잭션(T2)이 데이터를 추가하거나 삭제하여 결과가 달라지는 현상**
- 비슷한 개념으로 **Non-Repeatable Read는 같은 행(Row)의 값이 바뀌는 문제, Phantom Read는 행 자체가 추가/삭제되는 문제**

### **📌 예제**

1. **T1**: `SELECT COUNT(*) FROM orders WHERE amount > 1000;` → `5개`
2. **T2**: `INSERT INTO orders(amount) VALUES (1500); COMMIT;`
3. **T1**: `SELECT COUNT(*) FROM orders WHERE amount > 1000;` → `6개` (팬텀 행이 추가됨)

### **📌 해결 방법**

✅ **트랜잭션 격리 수준을 `SERIALIZABLE`로 설정 (완전한 직렬화)**

✅ **MySQL InnoDB의 경우, MVCC를 활용하여 `REPEATABLE READ`에서도 Phantom Read를 방지 가능**

---

## **4️⃣ Lost Update (손실된 업데이트)**

💡 **"여러 트랜잭션이 같은 데이터를 업데이트할 때, 하나의 업데이트가 사라지는 문제"**

### **📌 개념**

- 두 개 이상의 트랜잭션이 동시에 같은 데이터를 업데이트하고, **마지막 트랜잭션이 이전의 변경 사항을 덮어씌우는 문제**
- 동시성 문제 중 가장 심각한 문제 중 하나

### **📌 예제**

1. **T1**: `SELECT balance FROM accounts WHERE id = 1;` → `100`
2. **T2**: `SELECT balance FROM accounts WHERE id = 1;` → `100`
3. **T1**: `UPDATE accounts SET balance = 100 + 50 WHERE id = 1; COMMIT;`
4. **T2**: `UPDATE accounts SET balance = 100 - 30 WHERE id = 1; COMMIT;`
5. 최종 `balance = 70` → **T1의 `+50` 변경이 사라짐**

### **📌 해결 방법**

✅ **비관적 락 (Pessimistic Lock) 사용**

✅ **낙관적 락 (Optimistic Lock) 사용 (버전 필드 활용)**

---

## **5️⃣ Deadlock (교착 상태)**

💡 **"두 개 이상의 트랜잭션이 서로 상대방이 가진 락을 기다리면서 무한 대기 상태에 빠지는 문제"**

### **📌 개념**

- 트랜잭션 A가 리소스 1을 락을 걸고, 트랜잭션 B가 리소스 2에 락을 건 후, **각각 상대방의 락이 해제되기를 기다리는 상태**
- **이 상태가 지속되면 Deadlock 발생**

### **📌 예제**

1. **T1**: `UPDATE accounts SET balance = balance - 50 WHERE id = 1;` (`id = 1` 락)
2. **T2**: `UPDATE accounts SET balance = balance - 30 WHERE id = 2;` (`id = 2` 락)
3. **T1**: `UPDATE accounts SET balance = balance - 50 WHERE id = 2;` → **T2가 락 보유 중**
4. **T2**: `UPDATE accounts SET balance = balance - 30 WHERE id = 1;` → **T1이 락 보유 중**
5. **두 트랜잭션이 서로의 락이 해제되기를 기다리며 교착 상태(Deadlock) 발생**

### **📌 해결 방법**

✅ **트랜잭션 실행 순서를 일관되게 유지**
✅ **락 타임아웃 설정**

```sql
sql
복사편집
SET innodb_lock_wait_timeout = 5;

```

✅ **데드락이 감지되면 롤백 후 재시도 로직 구현**