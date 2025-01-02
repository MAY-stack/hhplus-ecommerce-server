### 시나리오 선정 및 프로젝트 Milestone
- 선정 시나리오: e-커머스 서비스
- Milestone

  ![milestone](https://github.com/user-attachments/assets/d8ede9e9-be60-4d41-b6d9-7bf45cc772f7)

### 요구사항 분석 자료
- 요구사항 분석 및 정책 설정

    ![image](https://github.com/user-attachments/assets/dc7589a4-15fe-4497-9e9c-710f2f3007a5)
- 시퀀스 다이어 그램
  - 포인트 충전·조회

    ![포인트 충전 조회](https://github.com/user-attachments/assets/14832e26-ca54-4288-8d9a-95cb761e6bc6)
  - 선착순 쿠폰 발급
  
    ![선착순 쿠폰 발급](https://github.com/user-attachments/assets/7cff6d0d-d9e0-470d-b6ab-818b2fb94f5d)
  - 발급 쿠폰 조회
 
    ![발급 쿠폰 조회](https://github.com/user-attachments/assets/ab5cc4bf-a3b6-4c18-8d4c-abe8c84d5ded)
  - 제품 조회
 
    ![제품 조회](https://github.com/user-attachments/assets/26b5f77d-ef41-41dd-b7e7-127859a20b9f)

  - 주문(+쿠폰 사용)
 
    ![주문(+쿠폰 사용)](https://github.com/user-attachments/assets/75872ff6-f41d-46ba-93e3-071c604459eb)

  - 결제
 
    ![결제](https://github.com/user-attachments/assets/9e242a90-d005-4c27-a2fe-196722676256)

  - 장바구니
 
    ![image](https://github.com/user-attachments/assets/610c91e3-f80f-4282-98a8-304ed955277c)
 
- 플로우 차트
  - 주문
    ![image](https://github.com/user-attachments/assets/bd8ae13d-be20-4567-b34a-d0655e643105)

  - 결제
    ![image](https://github.com/user-attachments/assets/87214159-f976-454c-88a5-a2f636a766ae)

- ERD
  ![image](https://github.com/user-attachments/assets/c8def9e3-ff71-48e8-8e08-17a9bcf06665)

  - users
    - 사용자 기본 정보
  - point, pointHistory
    - 사용자 ID와 포인트 잔액, 수정 일시
    - 포인트 충전/사용 내역
  - coupon
    - 쿠폰 틀
    - 쿠폰의 정보, 잔여수량 등 
  - couponIssuance
    - 쿠폰 발급번호(고유번호)를 포함한 발급된 쿠폰의 정보
  - product
    - 제품 기본 정보
  - order
    - 주문자, 주문 금액, 쿠폰 적용 정보 등 주문 정보
  - orderDetail
    - 주문에 포함된 제품 상세 정보
    - 주문 시의 제품 명, (nullable)옵션명, 제품 가격 포함
  - payment
    - 주문과 1:1 관계
    - 결제 정보
  - cart
    - 장바구니 정보
  - cartItem
    - 장바구니에 담긴 제품 정보
    - 구매 전의 제품 정보이기 때문에 제품id와 옵션id만 저장
      (제품명, 옵션명, 가격 등 변동시 반영됨)
