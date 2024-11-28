### **`STEP 19`**

- 부하 테스트 대상 선정 및 목적, 시나리오 등의 계획을 세우고 이를 문서로 작성
- 적합한 테스트 스크립트를 작성하고 수행
---

# 부하 테스트 보고서

## 1. 테스트 목적

---

테스트의 목적은 서비스 주요 API의 성능 및 안정성을 확인하고, 요청 처리 속도가 사용량 증가 시에도 안정적으로 유지되는지 검증하는 것입니다.
부하 테스트를 통해 각 API의 응답 시간, 처리량, 성공률 등의 성능 지표를 수집하고, 이를 InfluxDB와 Grafana를 활용해 시각화를 목적으로 합니다.

## 2. 테스트 대상

---

**테스트 대상 서비스**: HTTP 기반의 REST API  
**테스트 대상 API**:
- **상위 5개 상품 조회**: `/product/top5` (GET)
- **1차 잔액 충전**: `/user/charge` (POST)
- **랜덤 상품 조회**: `/product/{productId}` (GET)
- **2차 잔액 충전**: `/user/charge` (POST)
- **상품 주문**: `/order` (POST)

## 3. 테스트 환경

---

### 하드웨어
- **기기**: Apple MacBook Pro (M3 Pro)
- **프로세서**: M3 Pro (11코어 CPU, 14코어 GPU)
- **메모리 (RAM)**: 18GB
- **저장 공간 (SSD)**: 512GB
- **네트워크 환경**: 로컬 환경 (localhost)

### 소프트웨어
- **운영체제**: macOS Ventura (또는 최신 macOS 버전)
- **테스트 도구**: k6 (버전 0.43 이상)
- **스크립트 작성 언어**: JavaScript
- **데이터 저장 및 시각화**:
    - **InfluxDB**: 테스트 데이터를 저장하는 시계열 데이터베이스
    - **Grafana**: InfluxDB 데이터를 시각화하여 대시보드 제공

## 4. 테스트 조건 및 목표

---

### 테스트 조건:
- 동시 사용자 수: 최대 500명
- 테스트 시간: 1분
- 각 API의 95% 응답 시간이 500ms 미만이어야 함.

### 테스트 목표:
- 각 API의 성공률 95% 이상
- 테스트 결과를 InfluxDB에 저장하고, Grafana를 통해 시각화.

## 5. 테스트 시나리오

---

테스트 시나리오는 k6로 작성되었으며, 아래와 같은 API 호출 순서로 수행되었습니다.

### 1. 상위 5개 상품 조회
- **URL**: `/product/top5`
- **HTTP Method**: GET
- **성공 기준**: 상태 코드 200
- **메트릭 태그**: `name:상위_5개_상품_조회`

### 2. 1차 잔액 충전
- **URL**: `/user/charge`
- **HTTP Method**: POST
- **요청 데이터**: `userId`, `point` (랜덤 값)
- **성공 기준**: 상태 코드 200
- **메트릭 태그**: `name:1차_잔액_충전`

### 3. 랜덤 상품 조회
- **URL**: `/product/{productId}`
- **HTTP Method**: GET
- **성공 기준**: 상태 코드 200
- **메트릭 태그**: `name:상품_조회`

### 4. 2차 잔액 충전
- **URL**: `/user/charge`
- **HTTP Method**: POST
- **요청 데이터**: `userId`, `point` (랜덤 값)
- **성공 기준**: 상태 코드 200
- **메트릭 태그**: `name:2차_잔액_충전`

### 5. 상품 주문
- **URL**: `/order`
- **HTTP Method**: POST
- **요청 데이터**:
  ```json
  {
      "userId": "<랜덤 유저 ID>",
      "orderDetails": [
          { "productId": "<랜덤 상품 ID>", "quantity": "<랜덤 수량>" },
          { "productId": "<랜덤 상품 ID>", "quantity": "<랜덤 수량>" }
      ]
  }
- **성공 기준**: 상태 코드 200
- **메트릭 태그**: `name:상품_주문`


## 6. 테스트 스크립트 코드

```js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    stages: [
        { duration: '1m', target: 500 }, // 1분 동안 500명의 유저 유지
    ],
    thresholds: {
        'http_req_duration{name:상위_5개_상품_조회}': ['p(95)<500'], // 특정 요청의 95%가 500ms 미만
        'http_req_duration{name:1차_잔액_충전}': ['p(95)<500'],     // 특정 요청의 95%가 500ms 미만
        'http_req_duration{name:상품_조회}': ['p(95)<500'],         // 특정 요청의 95%가 500ms 미만
        'http_req_duration{name:2차_잔액_충전}': ['p(95)<500'],     // 특정 요청의 95%가 500ms 미만
        'http_req_duration{name:상품_주문}': ['p(95)<500'],         // 특정 요청의 95%가 500ms 미만
    },
};

const BASE_URL = 'http://localhost:8080';
const TOTAL_USERS = 10000; // 유저 수
const TOTAL_PRODUCTS = 10000; // 상품 수
const CHARGE_AMOUNT = 5000; // 랜덤 충전 금액 범위

export default function () {
    const userId = randomIntBetween(1, TOTAL_USERS);
    const productId = randomIntBetween(1, TOTAL_PRODUCTS);
    const chargeAmount = randomIntBetween(1000, CHARGE_AMOUNT);

    // 1. 상위 5개 상품 조회
    let res = http.get(`${BASE_URL}/product/top5`, { tags: { name: '상위_5개_상품_조회' } });
    check(res, { '상위 5개 상품 조회 성공': (r) => r.status === 200 });

    // 2. 잔액 충전 (1차 충전)
    const chargePayload1 = JSON.stringify({ userId, point: chargeAmount });
    const chargeHeaders = { 'Content-Type': 'application/json' };
    res = http.post(`${BASE_URL}/user/charge`, chargePayload1, { headers: chargeHeaders, tags: { name: '1차_잔액_충전' } });
    check(res, { '1차 잔액 충전 성공': (r) => r.status === 200 });

    // 3. 랜덤 상품 조회
    res = http.get(`${BASE_URL}/product/${productId}`, { tags: { name: '상품_조회' } });
    check(res, { '상품 조회 성공': (r) => r.status === 200 });

    // 4. 잔액 충전 (2차 충전)
    const chargePayload2 = JSON.stringify({ userId, point: chargeAmount });
    res = http.post(`${BASE_URL}/user/charge`, chargePayload2, { headers: chargeHeaders, tags: { name: '2차_잔액_충전' } });
    check(res, { '2차 잔액 충전 성공': (r) => r.status === 200 });

    // 5. 랜덤 상품 주문
    const orderPayload = JSON.stringify({
        userId: userId,
        orderDetails: [
            { productId: randomIntBetween(1, TOTAL_PRODUCTS), quantity: randomIntBetween(1, 5) },
            { productId: randomIntBetween(1, TOTAL_PRODUCTS), quantity: randomIntBetween(1, 3) },
        ],
    });
    const orderHeaders = { 'Content-Type': 'application/json' };
    res = http.post(`${BASE_URL}/order`, orderPayload, { headers: orderHeaders, tags: { name: '상품_주문' } });
    check(res, { '주문 성공': (r) => r.status === 200 });

    sleep(1); // 대기 시간 (1초)
}
