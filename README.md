## PR 목록

---

### [STEP05 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/8)
#### - 시나리오 선정 및 프로젝트 Milestone
#### - 시퀀스 다이어그램

### [STEP06 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/10)
#### - ERD 설계  
#### - API 명세 및 Mock API 작성
#### - 채택할 기본 패키지 구조, 기술 스택

### [DEFAULT & STEP07 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/12)
#### - 비지니스 로직 개발, 단위테스트 작성
#### - API Swagger기능 구현

### [DEFAULT & STEP09 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/14)
#### - 시스템 성격에 적합하게 Filter, Interceptor 를 활용해 기능의 관점을 분리하여 개선
#### - 모든 API 가 정상적으로 기능을 제공하도록 완성

### [STEP10 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/15)
#### - 시나리오별 동시성 통합 테스트 작성
#### - Chapter 2 회고록 작성

### [STEP11 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/16)
#### - 시나리오에서 발생할 수 있는 동시성 이슈에 대해 파악하고 가능한 동시성 제어 방식들을 도입해보고 각각의 장단점을 파악한 내용을 정리

### [STEP12 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/17)
#### - DB Lock 을 활용한 동시성 제어 방식에서 해당 비즈니스 로직에 적합하다고 판단한 동시성 제어 방식을 구현하여 비즈니스 로직에 적용하고, 통합 테스트 등으로 이를 검증하는 코드를 작성

### [분산락 / 데드락 개선 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/18)
#### - 주문 과정에서 비관적 락을 사용하고 있어, 여러 스레드가 동시에 주문을 처리할 때 데드락이 발생할 가능성이 있으므로 개선

### [STEP13 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/19)
#### - 조회가 오래 걸리는 쿼리에 대한 캐싱, 혹은 Redis 를 이용한 로직 이관을 통해 성능 개선할 수 있는 로직을 분석하고 이를 합리적인 이유와 함께 정리

### [STEP14 `확인하기`](https://github.com/NohYeongO/e-commerce_HH/pull/20)
#### - 캐싱 구현
