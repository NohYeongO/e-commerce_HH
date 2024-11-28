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
