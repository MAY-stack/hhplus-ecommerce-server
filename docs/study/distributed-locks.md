## **🔍 분산 락(Distributed Lock)이란?**

### **✅ 개념**

- **분산 시스템(Distributed System)** 환경에서 **여러 개의 서버(노드)가 동일한 리소스(데이터, 파일 등)에 동시에 접근하지 못하도록 락(Lock)을 거는 기술**
- **중앙 DB가 없는 경우**에도 **데이터 정합성을 유지하고, 동시성 문제를 방지**하는 역할
- **ex) 쇼핑몰의 재고 관리, 예약 시스템, 분산 캐시(예: Redis, Zookeeper)에서의 동기화**

---

## **🔒 왜 분산 락이 필요한가?**

일반적인 **DB 락**은 하나의 데이터베이스 내에서만 동작하지만,

분산 시스템에서는 여러 개의 서버(노드)가 존재하므로 **전통적인 락 방식이 동작하지 않음**

🔹 **예제 상황**: 쇼핑몰에서 `상품 재고 = 1` 인 상품에 대해 **동시에 2명의 고객이 결제**

- 만약 **서버 A와 서버 B에서 동시에 같은 상품을 결제**하면, **재고가 0이 되어야 하는데 2개가 결제될 수도 있음** → **데이터 정합성 문제 발생**
- 이를 방지하려면 **모든 서버에서 동일한 락을 공유해야 함** → **분산 락이 필요함**

---

## **🚀 분산 락을 구현하는 방법**

### **1️⃣ 데이터베이스 기반 분산 락 (DB Lock)**

- **특정 테이블에 락 정보를 저장**하여 락을 구현
- `SELECT ... FOR UPDATE` 또는 `INSERT INTO locks (resource_id, owner) VALUES (1, 'serverA')` 같은 방식 사용
- ✅ 장점: **설치가 간단하고, 트랜잭션이 보장됨**
- ❌ 단점: **DB 부하 증가, Deadlock 발생 가능**

**🔧 예제 (MySQL)**

```sql

BEGIN;
SELECT *
FROM locks
WHERE resource_id = 1 FOR
UPDATE;
-- 락을 획득한 후 해당 작업 수행
UPDATE products
SET stock = stock - 1
WHERE product_id = 100;
COMMIT;

```

**→ 하지만 DB가 단일 장애점(SPOF, Single Point of Failure)이 될 수 있음!**

---

### **2️⃣ Redis 기반 분산 락 (Redlock)**

- **Redis의 SETNX (SET if Not Exists) 명령어를 사용하여 락을 설정**
- 특정 키(예: `lock:product:100`)를 생성하여 락을 획득하고, 일정 시간 후 자동 해제
- ✅ 장점: **고성능, 빠른 처리 속도**
- ❌ 단점: **네트워크 장애나 노드 장애 시 락이 정상적으로 해제되지 않을 위험 있음**

**🔧 예제 (Redis)**

```bash

SET lock:product:100 "locked" NX PX 5000

```

- **NX** → 키가 존재하지 않을 때만 설정
- **PX 5000** → 5000ms(5초) 후 자동 해제 (Deadlock 방지)

### **🔹 Redlock 알고리즘 (Redis 클러스터에서 안정적인 락 구현)**

- 여러 개의 Redis 노드(일반적으로 5개)에서 락을 획득
- 과반수(3개 이상)에서 락을 성공적으로 획득하면 **유효한 락으로 인정**
- 일정 시간 이후 자동 해제하여 Deadlock 방지

🔹 **실제 사용 예제**: **Redisson (Java)**, **Spring Boot + Redis Lock**

---

### **3️⃣ Zookeeper 기반 분산 락 (ZNode)**

- **Zookeeper의 임시 노드(Ephemeral Node)를 활용하여 락을 구현**
- 임시 노드는 **연결이 끊어지면 자동으로 삭제됨** → 락 해제 보장
- ✅ 장점: **분산 환경에서 안정적인 락 구현 가능**
- ❌ 단점: **설정이 복잡하고 Zookeeper 자체가 오버헤드가 있음**

**🔧 예제 (Zookeeper)**

```java

InterProcessMutex lock=new InterProcessMutex(client,"/locks/product-100");
        lock.acquire();
// 락을 획득한 후 작업 수행
        lock.release();

```

🔹 **실제 사용 예제**: **Apache Curator 라이브러리**

---

## **🎯 분산 락 구현 비교**

| 방식                       | 원리                      | 장점         | 단점                 | 예제                |
|--------------------------|-------------------------|------------|--------------------|-------------------|
| **DB Lock**              | `SELECT ... FOR UPDATE` | 트랜잭션 지원    | 성능 저하, Deadlock 가능 | MySQL, PostgreSQL |
| **Redis Lock (Redlock)** | `SETNX + EXPIRE`        | 빠름, 간단함    | 네트워크 장애 시 문제 가능    | Redis, Redisson   |
| **Zookeeper Lock**       | 임시 노드 (Ephemeral Node)  | 안정적, 자동 해제 | 설정 복잡              | Apache Zookeeper  |

---

## **🔔 결론**

✅ **트랜잭션 기반 락이 필요하면** → **DB 락 사용** (`SELECT ... FOR UPDATE`)

✅ **고성능이 중요하면** → **Redis 기반 락 사용 (Redlock 알고리즘)**

✅ **완전한 분산 락이 필요하면** → **Zookeeper 기반 락 사용**

💡 **대부분의 경우, Redis 기반 락(Redlock)이 가장 많이 사용됨!**