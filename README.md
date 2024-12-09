# 📦 e-commerce 프로젝트

**항해 플러스 백엔드 과정** 중 진행한 **1인 백엔드 프로젝트**로, 전자상거래 시스템(e-commerce)에서 자주 사용되는 기능들을 구현한 프로젝트입니다.  
사용자는 상품 조회, 주문, 포인트 충전, 장바구니 관리 등 다양한 기능을 사용할 수 있으며, **성능 최적화와 안정성**을 고려한 설계와 개발을 목표로 삼았습니다.  
특히, **Redis 기반의 캐싱 및 분산 락**, **K6를 활용한 성능 테스트 및 최적화** 등을 적용해 실무 환경과 유사한 **운영 시뮬레이션**을 진행한 점이 강점입니다.

---

### **📌 주요 목표**
- 유지보수성과 확장성을 고려한 **도메인 중심 설계(Domain-Driven Design)** 및 **레이어드 아키텍처** 적용.
- **Redis 기반 캐싱**과 **락 메커니즘**을 활용해 동시성 문제 해결.
- **K6, Grafana, InfluxDB**를 사용한 성능 테스트 및 실시간 분석.
- **Testcontainers**를 활용한 컨테이너 기반 테스트 환경 구축.

---

## 🛠 사용 도구 (Tools)

| Language & Framework                                  | Database & Cache                                       | Monitoring & Metrics                                  | Testing                                               | Build Tools                                          | API Documentation                                    |
|-------------------------------------------------------|-------------------------------------------------------|------------------------------------------------------|------------------------------------------------------|-----------------------------------------------------|-----------------------------------------------------|
| ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green?logo=springboot) | ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql) ![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis) | ![Grafana](https://img.shields.io/badge/Grafana-Monitoring-orange?logo=grafana) ![InfluxDB](https://img.shields.io/badge/InfluxDB-Metrics-green?logo=influxdb) | ![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5) ![Testcontainers](https://img.shields.io/badge/Testcontainers-Integration-blue?logo=testcontainers) | ![Gradle](https://img.shields.io/badge/Gradle-Build-blue?logo=gradle) ![Spring](https://img.shields.io/badge/Dependency%20Management-Spring-lightgrey?logo=spring) | ![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-green?logo=swagger)                                               |

---

## ✨ 주요 활용 기술
1. **Swagger를 사용한 Mock API 구현**
   - Swagger UI 기반 API 문서화를 자동화하고, Mock API를 제공하여 빠른 개발 및 테스트 지원.

2. **JUnit을 활용한 단위 및 통합 테스트**
   - **JUnit 5**를 사용해 주요 비즈니스 로직의 단위 테스트(Unit Test)와 시스템 전반의 통합 테스트(Integration Test)를 구현.
   - **Testcontainers**로 MySQL과 Redis의 실제 컨테이너 환경을 구성하여 높은 신뢰성을 보장.

3. **Redis를 활용한 캐싱과 분산 락 적용**
   - Redis를 활용하여 주문 내역과 장바구니 데이터를 캐싱해 **조회 성능을 약 50% 이상 개선**.
   - 분산 환경에서도 데이터 동시성 문제를 방지하기 위해 **Redisson 기반 분산 락 메커니즘**을 구현.

4. **K6, Grafana, InfluxDB를 활용한 성능 시각화**
   - API의 부하 테스트를 통해 **처리 속도와 트래픽 한계**를 파악하고, 병목 구간을 해결.
   - 부하 테스트 결과를 Grafana로 시각화하여 **TPS, 응답 시간**, 시스템의 안정성을 지속적으로 모니터링.

---

## 📝 프로젝트를 통해 시도한 것과 배운 점

이번 프로젝트는 설계부터 기능 구현, 테스트까지 혼자 진행하며, 단순한 CRUD 구현을 넘어 다양한 도전과 학습을 할 수 있던 경험이었습니다.  
아래는 프로젝트를 통해 시도하고 배운 주요 내용을 정리한 것입니다.

---

### 🔧 **설계**
- 초기 ERD 설계와 API 설계 패키지 구조 설계까지 진행하면서 다양한 상황을 충분히 고려하지 못해, 기능 구현 단계에서 설계를 수정해야 하는 일이 많았습니다.
- 많은 시간이 소요됐지만 이런 과정을 통해 **설계 단계에서의 고민과 준비가 얼마나 중요한지** 깨닫게 되었습니다.

  - 📎 [초기 시퀀스 다이어그램](https://github.com/NohYeongO/e-commerce/pull/8)  
  - 📎 [초기 ERD 설계](https://github.com/NohYeongO/e-commerce/pull/10)  

---

### ⚙️ **동시성 제어**
- **Redis를 활용한 캐싱**과 **분산 락**을 적용하여, 분산 환경에서도 동시성 문제를 해결할 수 있는 구조를 구현했습니다.
- **비관적 락**을 사용한 동시성 제어와 **데드락 테스트**를 진행하며 락의 필요성과 활용 방법을 배울 수 있었습니다.

  - 📎 [동시성 제어 방식 학습](https://github.com/NohYeongO/e-commerce/pull/16)  
  - 📎 [데드락 테스트 및 분산 락 구현](https://github.com/NohYeongO/e-commerce/pull/18)  

---

### 🧪 **테스트 코드 작성**
- **JUnit**을 활용하여 **단위 테스트**와 **통합 테스트**를 작성했습니다.
- 특히, **Testcontainers**로 MySQL과 Redis를 컨테이너 환경에서 테스트 가능하도록 구성하여, 어떤 환경에서도 일관된 테스트를 진행할 수 있었습니다.

  - 📎 [테스트 컨테이너 설정 및 회고](https://github.com/NohYeongO/e-commerce/pull/15)  

---

### 📊 **성능 테스트와 시각화**
- **K6**로 부하 테스트를 진행하고, 결과 데이터를 **InfluxDB**에 저장한 뒤 **Grafana**로 시각화하여 성능 병목 구간을 분석했습니다.
- 성능 문제를 파악하고 최적화하는 과정을 통해, 실시간 성능 분석의 중요성을 경험할 수 있었습니다.

  - 📎 [캐싱 전략 구현](https://github.com/NohYeongO/e-commerce/pull/19)  
  - 📎 [부하 테스트 시나리오](https://github.com/NohYeongO/e-commerce/pull/23)  
  - 📎 [부하 테스트 시각화](https://github.com/NohYeongO/e-commerce/pull/24)  

---

### 🏗️ **아키텍처 설계**
- **레이어드 아키텍처**를 기반으로 **도메인 중심 설계(Domain-Driven Design)**를 적용했습니다.
- 도메인을 독립적으로 나누고 역할을 분리하여, 유지보수성과 확장성을 고려한 구조를 구현했습니다.

---

### 🎯 **한 줄 요약**
이 프로젝트는 단순한 기능 구현을 넘어, **설계와 테스트, 성능 최적화, 그리고 아키텍처 설계**까지 폭넓은 경험을 하며 성장할 수 있었던 중요한 기회였습니다.  
이를 통해 실무에서도 **복잡한 비즈니스 요구사항에 맞는 설계와 성능 최적화 솔루션을 제안**할 자신감을 얻게 되었습니다.  
앞으로 이벤트 기반 설계를 추가하여 **MSA 환경으로 발전**시키고 싶습니다.
