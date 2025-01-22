# **🚀 MVCC vs 낙관적 락(Optimistic Lock) 차이점**

**MVCC(Multi-Version Concurrency Control)** 와 **낙관적 락(Optimistic Lock)** 은 **동시성 제어(Concurrency Control)** 기법이라는 공통점이
있지만, **작동 방식과 목적이 다르다.**

---

## **✅ 1. MVCC(Multi-Version Concurrency Control)**

### **📌 개념**

- **다중 버전 동시성 제어** (**Multi-Version Concurrency Control**)
- **읽기(READ) 작업과 쓰기(WRITE) 작업이 충돌하지 않도록 하는 기법**
- 트랜잭션이 데이터를 읽을 때, **현재 값이 아니라 트랜잭션이 시작된 시점의 "스냅샷(snapshot)"을 읽음**
- 덕분에 **락을 사용하지 않고도 트랜잭션 격리 수준을 보장**할 수 있음

### **📌 MVCC의 특징**

✅ **읽기 작업(SELECT)은 락을 걸지 않음 → 동시성 향상**

✅ **쓰기 작업(UPDATE, DELETE)은 트랜잭션 버전에 따라 충돌을 해결**

✅ **낙관적 락과 달리, 충돌 발생 시 자동으로 해결됨**

✅ **PostgreSQL, MySQL InnoDB, Oracle 등에서 사용**

### **📌 MVCC 동작 방식**

- **트랜잭션이 데이터를 읽으면, 현재 변경된 데이터가 아니라 트랜잭션이 시작될 당시의 데이터 버전을 읽음**
- **데이터의 수정(UPDATE)은 새로운 버전을 생성하고, 이전 버전은 트랜잭션이 완료될 때까지 유지됨**
- **COMMIT이 완료된 데이터만 다른 트랜잭션에서 읽을 수 있도록 함**

**🔹 예제 (MySQL InnoDB MVCC)**

```sql
-- 트랜잭션 A (트랜잭션 시작)
START TRANSACTION;
SELECT balance
FROM accounts
WHERE id = 1;
-- 100 (스냅샷 읽기)
-- 트랜잭션 B (동시에 실행)
UPDATE accounts
SET balance = 200
WHERE id = 1;
COMMIT;
-- 트랜잭션 A (여전히 트랜잭션 A는 이전 값을 유지)
SELECT balance
FROM accounts
WHERE id = 1; -- 100 (MVCC 스냅샷 유지)
COMMIT;

```

📌 **트랜잭션 A는 트랜잭션 시작 당시의 데이터를 계속 유지하므로 "일관성"이 유지됨**

---

## **✅ 2. 낙관적 락(Optimistic Lock)**

### **📌 개념**

- 낙관적 락(Optimistic Lock)은 **트랜잭션이 충돌이 적을 것이라고 가정하고 진행하는 방식**
- 데이터를 읽고 수정할 때, **데이터가 변경되지 않았음을 검증한 후 업데이트** (충돌 발생 시 롤백)
- 주로 **"버전 관리(Versioning)"** 를 통해 구현됨

### **📌 낙관적 락의 특징**

✅ **충돌이 적은 환경에서 효과적**

✅ **락을 사용하지 않고 동시성 확보**

✅ **충돌이 발생하면 재시도(rollback 후 재시도 필요)**

✅ **JPA, Hibernate, Spring Data JPA에서 지원 (`@Version` 어노테이션 사용)**

### **📌 낙관적 락 동작 방식**

1. **데이터를 읽을 때, "버전(version)" 정보를 함께 가져옴**
2. **업데이트할 때, 기존 버전과 현재 버전을 비교**
3. **만약 버전이 변경되지 않았다면 업데이트 수행, 변경되었다면 충돌 발생 (Rollback 후 재시도 필요)**

**🔹 예제 (Spring Data JPA에서 낙관적 락 사용)**

```java

@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    private Integer balance;

    @Version  // 버전 관리 필드
    private Long version;
}

```

```java
@Transactional
public void updateBalance(Long accountId,Integer newBalance){
        Account account=accountRepository.findById(accountId)
        .orElseThrow(()->new RuntimeException("계좌 없음"));

        account.setBalance(newBalance);
        accountRepository.save(account); // 여기서 @Version 필드를 검증하여 충돌 발생 시 예외 발생
        }

```

📌 **버전 번호가 충돌하면 `OptimisticLockException`이 발생하여 트랜잭션이 롤백됨**

---

## **✅ 3. MVCC vs 낙관적 락 차이점**

| 비교 항목       | **MVCC** (Multi-Version Concurrency Control) | **낙관적 락** (Optimistic Lock)         |
|-------------|----------------------------------------------|-------------------------------------|
| **주요 개념**   | 여러 버전(snapshot)을 유지하여 충돌 방지                  | 버전 번호를 비교하여 충돌 감지                   |
| **목적**      | 읽기(SELECT) 성능 향상 & 동시성 유지                    | 충돌이 적은 환경에서 효율적인 동시성 관리             |
| **락 사용 여부** | ❌ No Lock (SELECT 시 락 없음)                    | ❌ No Lock (단, 충돌 시 재시도 필요)          |
| **충돌 발생 시** | 자동 해결 (트랜잭션 시작 시점 기준)                        | 충돌 발생 시 트랜잭션 롤백 후 재시도 필요            |
| **적용 대상**   | **PostgreSQL, MySQL InnoDB, Oracle**         | **JPA, Hibernate, Spring Data JPA** |
| **장점**      | **읽기 성능 높음, 동시성 우수**                         | **충돌이 적으면 성능 좋음, 락 없음**             |
| **단점**      | 쓰기 작업이 많으면 버전 데이터가 많아질 수 있음                  | 충돌이 많으면 계속 재시도해야 함                  |

---

## **✅ 4. MVCC와 낙관적 락, 언제 사용해야 할까?**

| 시나리오                               | MVCC 사용 여부       | 낙관적 락 사용 여부     |
|------------------------------------|------------------|-----------------|
| **읽기(SELECT) 작업이 많고, 동시성이 중요할 때**  | ✅ 사용             | ❌ 사용 X          |
| **업데이트(UPDATE)가 많고, 충돌 가능성이 높을 때** | ❌ 비효율적           | ✅ 사용            |
| **트랜잭션이 길어질 가능성이 있을 때**            | ✅ MVCC가 유리       | ❌ 충돌 발생 가능성 증가  |
| **JPA, Hibernate를 사용할 때**          | ❌ MVCC 직접 사용 어려움 | ✅ `@Version` 활용 |
| **데이터 충돌이 거의 없는 경우**               | ❌ 불필요            | ✅ 낙관적 락이 유리     |

---

## **🚀 결론**

✅ **MVCC** → **"읽기(SELECT) 성능을 높이고, 동시성을 보장"**하는 기법 (PostgreSQL, MySQL InnoDB)

✅ **낙관적 락** → **"충돌이 적은 경우 락 없이 동시성 보장"**하는 기법 (JPA, Hibernate `@Version`)

💡 **즉, MVCC는 DBMS에서 내부적으로 관리하는 동시성 제어 방식이고, 낙관적 락은 애플리케이션 레벨에서 충돌을 감지하는 방식이다!**