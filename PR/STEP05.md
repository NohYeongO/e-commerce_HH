## STEP05 > CHECKLIST
### **`STEP 05`**

- [x] 시나리오 선정 및 프로젝트 Milestone 제출
- 시나리오 요구사항 별 분석 자료 제출
    - [x] 시퀀스 다이어그램
- [x] 자료들을 리드미에 작성 후 PR 링크 제출

---
## **`STEP 05`**
## STEP 05_1. Milestone
<img height="" src="Milestone.png" width="1200"/>

## 1. 시퀀스 다이어그램 작성 (#1)
- **기간**: 10월 7일 (월) ~ 10월 8일 (화)
- **내용**: 시스템 전반의 흐름을 정의하는 시퀀스 다이어그램 작성

## 2. ERD 설계 (#2)
- **기간**: 10월 8일 (화) ~ 10월 9일 (수)
- **내용**: 데이터베이스 설계를 위한 ERD(Entity-Relationship Diagram) 작성

## 3. 패키지 구조 설계 (#4)
- **기간**: 10월 9일 (수) ~ 10월 10일 (금)
- **내용**: 시스템 패키지 구조 정의 및 설계

## 4. API 명세 및 Mock API 작성 (#3)
- **기간**: 10월 9일 (수) ~ 10월 10일 (금)
- **내용**: API 명세서 작성 및 Mock API 구성

## 5. 상품 조회 API, 잔액 충전/조회 API 기능 구현 및 테스트 (#5)
- **기간**: 10월 11일 (토) ~ 10월 15일 (수)
- **내용**: 상품 조회 API 및 잔액 충전/조회 API 기능 구현 및 테스트

## 6. 주문/결제 API, 상위상품 조회 API 기능 구현 및 테스트 (#6)
- **기간**: 10월 16일 (목) ~ 10월 19일 (일)
- **내용**: 주문 및 결제 관련 API와 상위상품 조회 API 기능 구현 및 테스트

## 7. 오류 수정 및 심화 과제 구현 (#7)
- **기간**: 10월 20일 (월) ~ 10월 25일 (금)
- **내용**: 오류 수정 및 심화 과제 기능 추가 구현

---


## STEP05_2. 시퀀스 다이어그램

## **`잔액충전/조회 API`**

### 1. **잔액 충전/조회 API**
- **핵심**: 클라이언트가 회원 ID로 잔액 충전 요청을 보내면, 데이터베이스에서 현재 잔액을 조회하고 충전 금액을 더한 후 최종 잔액을 저장합니다. **트랜잭션**을 사용하여 데이터 일관성을 유지하고, 오류 발생 시 **트랜잭션을 롤백**합니다.

```mermaid
sequenceDiagram
    participant Client
    participant Presentation Layer
    participant Business Layer
    participant Infrastructure Layer
    participant DB
  
    Client ->> Presentation Layer: 잔액 충전 요청(회원ID)
    Presentation Layer ->> Business Layer: 잔액 조회 및 충전 요청
  
    Business Layer ->> Infrastructure Layer: 잔액 조회 요청
    Infrastructure Layer ->> DB: 잔액 조회 쿼리 실행
    DB -->> Infrastructure Layer: 조회된 잔액 반환
    Infrastructure Layer -->> Business Layer: 조회된 잔액 반환
  
    Business Layer ->> Business Layer: 잔액 수정 (충전 금액 더하기)
    Note over Business Layer: 잔액 수정
  
    Business Layer ->> Infrastructure Layer: 수정된 잔액 저장 요청
    Infrastructure Layer ->> DB: 수정된 잔액 저장 쿼리 실행
    DB -->> Infrastructure Layer: 저장 완료 응답
    Infrastructure Layer -->> Business Layer: 저장 완료 반환
  
    Business Layer -->> Presentation Layer: 수정된 잔액 반환
    Presentation Layer -->> Client: 충전 완료 및 최종 잔액 응답
  
    alt 충전 중 에러 발생 시
      Business Layer ->> Infrastructure Layer: 트랜잭션 롤백 요청
      Infrastructure Layer ->> DB: 롤백 쿼리 실행
      DB -->> Infrastructure Layer: 롤백 완료 응답
      Infrastructure Layer -->> Business Layer: 롤백 완료 응답
      Business Layer -->> Presentation Layer: 충전 실패 응답
      Presentation Layer -->> Client: 충전 실패 메시지 반환
    end
```
## **`상품조회 API`**

### 2. **상품 조회 API**
- **핵심**: 클라이언트가 상품 조회 요청을 보내면, 데이터베이스에서 해당 상품의 가격과 재고를 조회하여 반환합니다. 상품이 존재하지 않으면 404 응답을 반환합니다.

```mermaid
sequenceDiagram
    participant Client
    participant Presentation Layer
    participant Business Layer
    participant Infrastructure Layer
    participant DB

    Client ->> Presentation Layer: 상품 조회 요청 (상품 ID)
    Presentation Layer ->> Business Layer: 상품 조회 요청 전달

    Business Layer ->> Infrastructure Layer: 상품 정보 조회 요청
    Infrastructure Layer ->> DB: 상품 정보 조회 쿼리 실행
    
    alt 상품이 존재하는 경우
        DB -->> Infrastructure Layer: 상품 정보 반환 (ID, 이름, 가격, 잔여 수량)
        Infrastructure Layer -->> Business Layer: 상품 정보 반환
        Business Layer -->> Presentation Layer: 상품 정보 반환
        Presentation Layer -->> Client: 상품 정보 응답 (ID, 이름, 가격, 잔여 수량)
    else 상품이 없는 경우
        DB -->> Infrastructure Layer: 상품 없음 (Null)
        Infrastructure Layer -->> Business Layer: 상품을 찾을 수 없음 예외 발생
        Business Layer -->> Presentation Layer: 예외 전달
        Presentation Layer -->> Client: 응답 (404 Not Found)
    end
```
## **`주문/결제 API`**
### 3. **주문/결제 API**
- **핵심**: 여러 상품을 주문할 때, 각 상품의 **재고에 비관적 락**을 걸어 다른 트랜잭션이 동시에 재고를 수정하지 못하게 합니다. 잔액 확인 후 결제 성공 시 재고와 잔액을 차감하고 주문 정보를 외부 데이터 플랫폼에 전송합니다. 재고나 잔액 부족 시 **트랜잭션 롤백**으로 처리합니다.

```mermaid
sequenceDiagram
    participant Client
    participant Presentation Layer
    participant Business Layer
    participant Infrastructure Layer
    participant DB
    participant Data Platform

    Client ->> Presentation Layer: 주문 및 결제 요청 (회원ID, 상품ID 리스트, 수량 리스트)
    Presentation Layer ->> Business Layer: 주문 처리 요청

    Business Layer ->> Infrastructure Layer: 회원 잔액 조회 요청
    Infrastructure Layer ->> DB: 회원 잔액 조회 쿼리 실행
    DB -->> Infrastructure Layer: 회원 잔액 반환
    Infrastructure Layer -->> Business Layer: 회원 잔액 반환
    
    alt 잔액 부족 시
        Business Layer -->> Presentation Layer: 결제 실패 응답 (잔액 부족)
        Presentation Layer -->> Client: 결제 실패 메시지 반환
        Note right of Business Layer: 트랜잭션 롤백
    else 잔액 충분 시
      Business Layer ->> Infrastructure Layer: 상품 리스트 재고 조회 요청 (비관적 락)
      Infrastructure Layer ->> DB: 상품 재고 조회 쿼리 실행 (비관적 락 걸기)
      DB -->> Infrastructure Layer: 상품 리스트 재고 반환 (락 유지)
      Infrastructure Layer -->> Business Layer: 상품 재고 반환
    end

    alt 재고 부족 시
        Business Layer -->> Presentation Layer: 결제 실패 응답 (재고 부족)
        Presentation Layer -->> Client: 결제 실패 메시지 반환
        Note right of Business Layer: 트랜잭션 롤백 및 락 해제
    else 재고 충분 시
        Business Layer ->> Infrastructure Layer: 상품 재고 차감 요청
        Infrastructure Layer ->> DB: 상품 재고 업데이트 쿼리 실행
        DB -->> Infrastructure Layer: 상품 재고 업데이트 완료 응답

        Business Layer ->> Infrastructure Layer: 회원 잔액 차감 요청
        Infrastructure Layer ->> DB: 회원 잔액 차감 쿼리 실행
        DB -->> Infrastructure Layer: 잔액 차감 완료 응답
        Infrastructure Layer -->> Business Layer: 잔액 차감 성공 응답

        Business Layer ->> Data Platform: 주문 내역 데이터 전송
        Data Platform -->> Business Layer: 데이터 전송 성공 응답

        Business Layer -->> Presentation Layer: 주문 성공 응답
        Presentation Layer -->> Client: 결제 성공 및 잔액 차감 완료 응답
    end
```    
## **`상위 상품 조회 API`**
### 4. **상위 상품 조회 API**
- **핵심**: 클라이언트가 상위 5개 상품 조회 요청을 보내면, 최근 3일간 가장 많이 판매된 상위 5개 상품 데이터를 데이터베이스에서 가져와 클라이언트에 반환합니다.
```mermaid   
sequenceDiagram
    participant Client
    participant Presentation Layer
    participant Business Layer
    participant DB


    Client ->> Presentation Layer: 상위 5개 상품 조회 요청
    Presentation Layer ->> Business Layer: 상위 상품 조회 요청

    Business Layer ->> DB: 최근 3일간 판매 상위 5개 데이터 요청
    DB -->> Business Layer: 상위 5개 상품 판매 데이터 반환

    Business Layer -->> Presentation Layer: 상위 5개 상품 정보 반환
    Presentation Layer -->> Client: 상위 5개 상품 정보 응답
```